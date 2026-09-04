package com.ideiasmidias.media.service;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Stores uploads on the local filesystem, under {@code app.media.upload-dir}.
 * This is the zero-config default for local development; switch to
 * {@code app.storage.provider=r2} (see {@link S3MediaStorageServiceImpl}) for
 * production so uploads survive redeploys and work across multiple instances.
 */
@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalMediaStorageServiceImpl implements MediaStorageService {

    private final MediaFileValidator validator;

    @Value("${app.media.upload-dir:uploads/media}")
    private String uploadDir;

    @Value("${app.media.public-url-prefix:/uploads/media/}")
    private String publicUrlPrefix;

    public LocalMediaStorageServiceImpl(MediaFileValidator validator) {
        this.validator = validator;
    }

    @Override
    public StoredMediaFile store(MultipartFile file) {
        MediaFileValidator.ValidatedFile validated = validator.validate(file);
        String storedFileName = UUID.randomUUID() + validated.extension();

        Path uploadPath = getUploadPath();
        Path targetPath = uploadPath.resolve(storedFileName).normalize();

        if (!targetPath.startsWith(uploadPath)) {
            throw new BadRequestException("Invalid file path");
        }

        try {
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store uploaded file", ex);
        }

        return new StoredMediaFile(
                storedFileName,
                validated.originalName(),
                buildPublicFileUrl(storedFileName),
                validated.mediaType(),
                validated.mimeType(),
                file.getSize()
        );
    }

    @Override
    public StoredMediaFile storeGeneratedFile(Path filePath, String mimeType, MediaType mediaType) {
        String extension = mimeType.equals("video/mp4") ? ".mp4" : "";
        String storedFileName = UUID.randomUUID() + extension;

        Path uploadPath = getUploadPath();
        Path targetPath = uploadPath.resolve(storedFileName).normalize();

        if (!targetPath.startsWith(uploadPath)) {
            throw new BadRequestException("Invalid file path");
        }

        try {
            Files.createDirectories(uploadPath);
            Files.copy(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store generated file", ex);
        }

        long size;
        try {
            size = Files.size(targetPath);
        } catch (IOException ex) {
            size = 0L;
        }

        return new StoredMediaFile(
                storedFileName,
                storedFileName,
                buildPublicFileUrl(storedFileName),
                mediaType,
                mimeType,
                size
        );
    }

    @Override
    public void deleteByFileUrl(String fileUrl) {
        Path targetPath = resolveStoredPath(fileUrl);
        if (targetPath == null) {
            return;
        }

        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete stored file", ex);
        }
    }

    @Override
    public InputStream openStream(String fileUrl) {
        Path targetPath = resolveStoredPath(fileUrl);
        if (targetPath == null) {
            throw new BadRequestException("Invalid file path");
        }

        try {
            return Files.newInputStream(targetPath);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read stored file", ex);
        }
    }

    private Path resolveStoredPath(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return null;
        }

        String pathValue = fileUrl;

        try {
            if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
                pathValue = URI.create(fileUrl).getPath();
            }
        } catch (Exception ignored) {
        }

        Path fileNamePath = Paths.get(pathValue).getFileName();
        if (fileNamePath == null) {
            return null;
        }

        String fileName = fileNamePath.toString();
        if (fileName.isBlank()) {
            return null;
        }

        Path uploadPath = getUploadPath();
        Path targetPath = uploadPath.resolve(fileName).normalize();

        if (!targetPath.startsWith(uploadPath)) {
            return null;
        }

        return targetPath;
    }

    private Path getUploadPath() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    private String buildPublicFileUrl(String storedFileName) {
        String prefix = publicUrlPrefix == null || publicUrlPrefix.isBlank()
                ? "/uploads/media/"
                : publicUrlPrefix.trim();

        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }

        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }

        return prefix + storedFileName;
    }
}
