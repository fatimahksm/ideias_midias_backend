package com.ideiasmidias.media.service;

import com.ideiasmidias.adminuser.entity.AdminUser;
import com.ideiasmidias.adminuser.repository.AdminUserRepository;
import com.ideiasmidias.common.enums.MediaProcessingStatus;
import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.common.request.PageRequestFactory;
import com.ideiasmidias.common.response.PageResponse;
import com.ideiasmidias.media.dto.MediaLibraryResponse;
import com.ideiasmidias.media.entity.MediaLibrary;
import com.ideiasmidias.media.repository.MediaLibraryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MediaLibraryServiceImpl implements MediaLibraryService {

    private final MediaLibraryRepository mediaLibraryRepository;
    private final AdminUserRepository adminUserRepository;
    private final MediaStorageService mediaStorageService;
    private final VideoTranscodingService videoTranscodingService;

    @Value("${app.media.video.transcode-enabled:true}")
    private boolean videoTranscodeEnabled;

    @Override
    public MediaLibraryResponse upload(MultipartFile file, Long uploadedById) {
        if (uploadedById == null) {
            throw new BadRequestException("Uploader admin id is required");
        }

        AdminUser uploadedBy = adminUserRepository.findById(uploadedById)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + uploadedById));

        boolean shouldTranscode = videoTranscodeEnabled
                && isVideoFile(file)
                && !"video/mp4".equalsIgnoreCase(normalizedContentType(file));

        // store() reads the file via getInputStream() first, which every
        // MultipartFile implementation supports repeatably. Only after that
        // do we take our own copy for ffmpeg, so neither read can starve the
        // other regardless of the underlying multipart implementation.
        StoredMediaFile storedFile = mediaStorageService.store(file);
        Path tempInputPath = shouldTranscode ? copyToTempFile(file) : null;

        MediaLibrary media = new MediaLibrary();
        media.setFileName(storedFile.storedFileName());
        media.setOriginalName(storedFile.originalName());
        media.setFileUrl(storedFile.fileUrl());
        media.setFileType(storedFile.fileType());
        media.setMimeType(storedFile.mimeType());
        media.setFileSize(storedFile.fileSize());
        media.setProcessingStatus(
                tempInputPath != null ? MediaProcessingStatus.PROCESSING : MediaProcessingStatus.READY);
        media.setUploadedBy(uploadedBy);

        try {
            MediaLibrary saved = mediaLibraryRepository.save(media);

            log.info(
                    "Media uploaded successfully. mediaId={}, uploadedById={}, fileType={}, fileUrl={}",
                    saved.getId(),
                    uploadedById,
                    saved.getFileType(),
                    saved.getFileUrl()
            );

            if (tempInputPath != null) {
                videoTranscodingService.transcode(saved.getId(), tempInputPath, storedFile.fileUrl());
            }

            return mapToResponse(saved);
        } catch (RuntimeException ex) {
            log.error(
                    "Media database save failed after file storage. uploadedById={}, fileUrl={}, errorType={}, message={}",
                    uploadedById,
                    storedFile.fileUrl(),
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    ex
            );

            try {
                mediaStorageService.deleteByFileUrl(storedFile.fileUrl());
            } catch (RuntimeException cleanupEx) {
                log.error(
                        "Stored media cleanup failed after save error. fileUrl={}, errorType={}, message={}",
                        storedFile.fileUrl(),
                        cleanupEx.getClass().getSimpleName(),
                        cleanupEx.getMessage(),
                        cleanupEx
                );
            }

            throw ex;
        }
    }

    @Override
    @Transactional
    public MediaLibraryResponse getById(Long id) {
        return mapToResponse(getEntityById(id));
    }

    @Override
    @Transactional
    public List<MediaLibraryResponse> getAll() {
        return mediaLibraryRepository.findAllByOrderByIdDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public PageResponse<MediaLibraryResponse> getPage(
            MediaType fileType,
            Long uploaderId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequestFactory.of(page, size);

        Page<MediaLibrary> result;

        if (uploaderId != null && fileType != null) {
            result = mediaLibraryRepository
                    .findAllByUploadedBy_IdAndFileTypeOrderByIdDesc(uploaderId, fileType, pageable);
        } else if (uploaderId != null) {
            result = mediaLibraryRepository.findAllByUploadedBy_IdOrderByIdDesc(uploaderId, pageable);
        } else if (fileType != null) {
            result = mediaLibraryRepository.findAllByFileTypeOrderByIdDesc(fileType, pageable);
        } else {
            result = mediaLibraryRepository.findAllByOrderByIdDesc(pageable);
        }

        return PageResponse.from(result, this::mapToResponse);
    }

    @Override
    @Transactional
    public List<MediaLibraryResponse> getByType(MediaType fileType) {
        return mediaLibraryRepository.findAllByFileTypeOrderByIdDesc(fileType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<MediaLibraryResponse> getByUploader(Long adminUserId) {
        return mediaLibraryRepository.findAllByUploadedBy_IdOrderByIdDesc(adminUserId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<MediaLibraryResponse> getByUploaderAndType(Long adminUserId, MediaType fileType) {
        return mediaLibraryRepository.findAllByUploadedBy_IdAndFileTypeOrderByIdDesc(adminUserId, fileType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        MediaLibrary media = getEntityById(id);
        String fileUrl = media.getFileUrl();

        mediaLibraryRepository.delete(media);

        log.info(
                "Media library record deleted. mediaId={}, fileUrl={}",
                id,
                fileUrl
        );

        try {
            mediaStorageService.deleteByFileUrl(fileUrl);
        } catch (RuntimeException ex) {
            log.error(
                    "Media file cleanup failed after record deletion. mediaId={}, fileUrl={}, errorType={}, message={}",
                    id,
                    fileUrl,
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    ex
            );
        }
    }

    private MediaLibrary getEntityById(Long id) {
        return mediaLibraryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media library item not found with id: " + id));
    }

    private MediaLibraryResponse mapToResponse(MediaLibrary media) {
        return MediaLibraryResponse.builder()
                .id(media.getId())
                .fileName(media.getFileName())
                .originalName(media.getOriginalName())
                .fileUrl(media.getFileUrl())
                .fileType(media.getFileType())
                .mimeType(media.getMimeType())
                .fileSize(media.getFileSize())
                .processingStatus(media.getProcessingStatus())
                .uploadedById(media.getUploadedBy() != null ? media.getUploadedBy().getId() : null)
                .createdAt(media.getCreatedAt())
                .updatedAt(media.getUpdatedAt())
                .build();
    }

    private boolean isVideoFile(MultipartFile file) {
        String contentType = normalizedContentType(file);
        return contentType.startsWith("video/");
    }

    private String normalizedContentType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType == null ? "" : contentType.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private Path copyToTempFile(MultipartFile file) {
        try {
            Path tempFile = Files.createTempFile("upload-in-", resolveTempSuffix(file));
            try (java.io.InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            return tempFile;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to buffer uploaded video for transcoding", ex);
        }
    }

    private String resolveTempSuffix(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            return ".tmp";
        }

        int lastDot = originalName.lastIndexOf('.');
        return lastDot >= 0 ? originalName.substring(lastDot) : ".tmp";
    }
}