package com.ideiasmidias.media.service;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class MediaStorageServiceImpl implements MediaStorageService {

    private static final Set<String> ALLOWED_IMAGE_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private static final Set<String> ALLOWED_VIDEO_MIME_TYPES = Set.of(
            "video/mp4",
            "video/webm",
            "video/quicktime"
    );

    @Value("${app.media.upload-dir:uploads/media}")
    private String uploadDir;

    @Value("${app.media.public-url-prefix:/uploads/media/}")
    private String publicUrlPrefix;

    @Value("${app.media.max-file-size-bytes:10485760}")
    private long maxFileSizeBytes;

    @Override
    public StoredMediaFile store(MultipartFile file) {
        validateFile(file);

        String originalName = sanitizeOriginalName(file.getOriginalFilename());
        String mimeType = normalizeMimeType(file.getContentType());
        MediaType mediaType = resolveMediaType(mimeType);
        String extension = resolveExtension(originalName, mimeType);
        String storedFileName = UUID.randomUUID() + extension;

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

        String fileUrl = buildPublicFileUrl(storedFileName);

        return new StoredMediaFile(
                storedFileName,
                originalName,
                fileUrl,
                mediaType,
                mimeType,
                file.getSize()
        );
    }

    @Override
    public void deleteByFileUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
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
            return;
        }

        String fileName = fileNamePath.toString();
        if (fileName.isBlank()) {
            return;
        }

        Path uploadPath = getUploadPath();
        Path targetPath = uploadPath.resolve(fileName).normalize();

        if (!targetPath.startsWith(uploadPath)) {
            return;
        }

        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete stored file", ex);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        if (file.getSize() > maxFileSizeBytes) {
            throw new BadRequestException("File size exceeds the allowed limit");
        }

        String mimeType = normalizeMimeType(file.getContentType());
        resolveMediaType(mimeType);
    }

    private MediaType resolveMediaType(String mimeType) {
        if (ALLOWED_IMAGE_MIME_TYPES.contains(mimeType)) {
            return MediaType.IMAGE;
        }

        if (ALLOWED_VIDEO_MIME_TYPES.contains(mimeType)) {
            return MediaType.VIDEO;
        }

        throw new BadRequestException("Unsupported file type. Only image and video uploads are allowed");
    }

    private String resolveExtension(String originalName, String mimeType) {
        int lastDot = originalName.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < originalName.length() - 1) {
            String ext = originalName.substring(lastDot).toLowerCase(Locale.ROOT);
            if (ext.matches("\\.[a-z0-9]{1,10}")) {
                return ext;
            }
        }

        return switch (mimeType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            case "video/mp4" -> ".mp4";
            case "video/webm" -> ".webm";
            case "video/quicktime" -> ".mov";
            default -> "";
        };
    }

    private String sanitizeOriginalName(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            return "file";
        }

        Path fileNamePath = Paths.get(originalName).getFileName();
        if (fileNamePath == null) {
            return "file";
        }

        String fileName = fileNamePath.toString().trim();
        return fileName.isBlank() ? "file" : fileName;
    }

    private String normalizeMimeType(String mimeType) {
        return mimeType == null ? "" : mimeType.trim().toLowerCase(Locale.ROOT);
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