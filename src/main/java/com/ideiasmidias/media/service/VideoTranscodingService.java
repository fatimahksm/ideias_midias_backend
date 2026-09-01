package com.ideiasmidias.media.service;

import com.ideiasmidias.common.enums.MediaProcessingStatus;
import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.media.entity.MediaLibrary;
import com.ideiasmidias.media.repository.MediaLibraryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Normalizes every uploaded video to H.264/AAC MP4 in the background. iPhones
 * default to HEVC-in-QuickTime, which plays fine in Safari but is unreliable
 * (or outright unsupported) in Chrome and Firefox — this removes that gap so
 * a video uploaded from any device plays back the same everywhere.
 *
 * The raw upload is stored and usable immediately; this swaps it for the
 * transcoded version once ffmpeg finishes, so there is no broken/missing
 * video while processing is in flight.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoTranscodingService {

    private static final long TRANSCODE_TIMEOUT_MINUTES = 15;

    private final MediaStorageService mediaStorageService;
    private final MediaLibraryRepository mediaLibraryRepository;

    @Value("${app.media.video.ffmpeg-path:ffmpeg}")
    private String ffmpegPath;

    @Async("videoTranscodeExecutor")
    public void transcode(Long mediaId, Path tempInputPath, String rawFileUrl) {
        Path tempOutputPath = null;

        try {
            tempOutputPath = Files.createTempFile("transcode-out-", ".mp4");

            boolean success = runFfmpeg(tempInputPath, tempOutputPath);

            if (!success) {
                markFailed(mediaId);
                return;
            }

            StoredMediaFile transcoded = mediaStorageService.storeGeneratedFile(
                    tempOutputPath, "video/mp4", MediaType.VIDEO);

            applyTranscodedResult(mediaId, transcoded);

            mediaStorageService.deleteByFileUrl(rawFileUrl);

            log.info("Video transcode finished. mediaId={}, newFileUrl={}", mediaId, transcoded.fileUrl());
        } catch (Exception ex) {
            log.error("Video transcode failed. mediaId={}, errorType={}, message={}",
                    mediaId, ex.getClass().getSimpleName(), ex.getMessage(), ex);
            markFailed(mediaId);
        } finally {
            deleteQuietly(tempInputPath);
            deleteQuietly(tempOutputPath);
        }
    }

    private boolean runFfmpeg(Path input, Path output) throws IOException, InterruptedException {
        List<String> command = List.of(
                ffmpegPath,
                "-y",
                "-i", input.toString(),
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-crf", "23",
                "-c:a", "aac",
                "-b:a", "128k",
                "-movflags", "+faststart",
                output.toString()
        );

        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

        boolean finished = process.waitFor(TRANSCODE_TIMEOUT_MINUTES, TimeUnit.MINUTES);

        if (!finished) {
            process.destroyForcibly();
            log.error("ffmpeg transcode timed out after {} minutes", TRANSCODE_TIMEOUT_MINUTES);
            return false;
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            log.error("ffmpeg exited with code {}", exitCode);
            return false;
        }

        return Files.exists(output) && Files.size(output) > 0;
    }

    private void applyTranscodedResult(Long mediaId, StoredMediaFile transcoded) {
        mediaLibraryRepository.findById(mediaId).ifPresent(media -> {
            media.setFileName(transcoded.storedFileName());
            media.setFileUrl(transcoded.fileUrl());
            media.setMimeType(transcoded.mimeType());
            media.setFileSize(transcoded.fileSize());
            media.setProcessingStatus(MediaProcessingStatus.READY);
            mediaLibraryRepository.save(media);
        });
    }

    private void markFailed(Long mediaId) {
        mediaLibraryRepository.findById(mediaId).ifPresent(media -> {
            media.setProcessingStatus(MediaProcessingStatus.FAILED);
            mediaLibraryRepository.save(media);
        });
    }

    private void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
