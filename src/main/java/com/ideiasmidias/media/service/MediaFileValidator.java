package com.ideiasmidias.media.service;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Shared upload validation for every {@link MediaStorageService} implementation
 * (local disk, S3/R2, ...). Keeps the mime/extension/magic-byte checks identical
 * no matter where the bytes end up.
 */
@Component
public class MediaFileValidator {

    private static final Set<String> ALLOWED_IMAGE_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif",
            "image/heic",
            "image/heif"
    );

    private static final Set<String> ALLOWED_VIDEO_MIME_TYPES = Set.of(
            "video/mp4",
            "video/webm",
            "video/quicktime"
    );

    private static final Map<String, Set<String>> ALLOWED_EXTENSIONS_BY_MIME = Map.ofEntries(
            Map.entry("image/jpeg", Set.of(".jpg", ".jpeg")),
            Map.entry("image/png", Set.of(".png")),
            Map.entry("image/webp", Set.of(".webp")),
            Map.entry("image/gif", Set.of(".gif")),
            Map.entry("image/heic", Set.of(".heic")),
            Map.entry("image/heif", Set.of(".heif")),
            Map.entry("video/mp4", Set.of(".mp4", ".m4v")),
            Map.entry("video/webm", Set.of(".webm")),
            Map.entry("video/quicktime", Set.of(".mov", ".qt"))
    );

    /** ISO-BMFF "ftyp" brands used by HEIC/HEIF stills (iPhone photos). */
    private static final Set<String> HEIC_FTYP_BRANDS = Set.of(
            "heic", "heix", "heim", "heis", "hevc", "hevx", "hevm", "hevs", "mif1", "msf1"
    );

    @Value("${app.media.max-file-size-bytes:10485760}")
    private long maxFileSizeBytes;

    public record ValidatedFile(String originalName, String mimeType, MediaType mediaType, String extension) {
    }

    public ValidatedFile validate(MultipartFile file) {
        String originalName = sanitizeOriginalName(file.getOriginalFilename());
        String mimeType = normalizeMimeType(file.getContentType());

        if (file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        if (file.getSize() <= 0) {
            throw new BadRequestException("File is empty");
        }

        if (file.getSize() > maxFileSizeBytes) {
            throw new BadRequestException("File size exceeds the allowed limit");
        }

        MediaType mediaType = resolveMediaType(mimeType);
        validateExtensionAgainstMime(originalName, mimeType);

        byte[] header = readHeader(file, 32);
        validateSignature(header, mimeType, mediaType);

        String extension = resolveExtension(originalName, mimeType);

        return new ValidatedFile(originalName, mimeType, mediaType, extension);
    }

    private MediaType resolveMediaType(String mimeType) {
        if (ALLOWED_IMAGE_MIME_TYPES.contains(mimeType)) {
            return MediaType.IMAGE;
        }

        if (ALLOWED_VIDEO_MIME_TYPES.contains(mimeType)) {
            return MediaType.VIDEO;
        }

        throw new BadRequestException("Unsupported file type. Only approved image and video uploads are allowed");
    }

    private void validateExtensionAgainstMime(String originalName, String mimeType) {
        String extension = extractExtension(originalName);

        if (extension == null) {
            return;
        }

        Set<String> allowedExtensions = ALLOWED_EXTENSIONS_BY_MIME.get(mimeType);
        if (allowedExtensions == null || !allowedExtensions.contains(extension)) {
            throw new BadRequestException("File extension does not match the provided content type");
        }
    }

    private String resolveExtension(String originalName, String mimeType) {
        String extension = extractExtension(originalName);

        if (extension != null) {
            Set<String> allowedExtensions = ALLOWED_EXTENSIONS_BY_MIME.get(mimeType);
            if (allowedExtensions != null && allowedExtensions.contains(extension)) {
                return extension;
            }

            throw new BadRequestException("File extension does not match the provided content type");
        }

        return switch (mimeType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            case "image/heic" -> ".heic";
            case "image/heif" -> ".heif";
            case "video/mp4" -> ".mp4";
            case "video/webm" -> ".webm";
            case "video/quicktime" -> ".mov";
            default -> throw new BadRequestException("Unsupported file type");
        };
    }

    private String extractExtension(String originalName) {
        int lastDot = originalName.lastIndexOf('.');
        if (lastDot < 0 || lastDot >= originalName.length() - 1) {
            return null;
        }

        String ext = originalName.substring(lastDot).toLowerCase(Locale.ROOT);

        if (!ext.matches("\\.[a-z0-9]{1,10}")) {
            throw new BadRequestException("Invalid file extension");
        }

        return ext;
    }

    private byte[] readHeader(MultipartFile file, int maxBytes) {
        try (InputStream inputStream = file.getInputStream()) {
            return inputStream.readNBytes(maxBytes);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to inspect uploaded file", ex);
        }
    }

    private void validateSignature(byte[] header, String mimeType, MediaType mediaType) {
        boolean valid = switch (mimeType) {
            case "image/jpeg" -> isJpeg(header);
            case "image/png" -> isPng(header);
            case "image/webp" -> isWebp(header);
            case "image/gif" -> isGif(header);
            case "image/heic", "image/heif" -> isHeic(header);
            case "video/mp4" -> isMp4(header);
            case "video/webm" -> isWebm(header);
            case "video/quicktime" -> isQuickTime(header);
            default -> false;
        };

        if (!valid) {
            throw new BadRequestException(
                    mediaType == MediaType.IMAGE
                            ? "Uploaded image content is invalid or corrupted"
                            : "Uploaded video content is invalid or corrupted"
            );
        }
    }

    private boolean isJpeg(byte[] header) {
        return header.length >= 3
                && (header[0] & 0xFF) == 0xFF
                && (header[1] & 0xFF) == 0xD8
                && (header[2] & 0xFF) == 0xFF;
    }

    private boolean isPng(byte[] header) {
        return header.length >= 8
                && (header[0] & 0xFF) == 0x89
                && header[1] == 0x50
                && header[2] == 0x4E
                && header[3] == 0x47
                && (header[4] & 0xFF) == 0x0D
                && (header[5] & 0xFF) == 0x0A
                && (header[6] & 0xFF) == 0x1A
                && (header[7] & 0xFF) == 0x0A;
    }

    private boolean isGif(byte[] header) {
        if (header.length < 6) {
            return false;
        }

        String signature = new String(header, 0, 6, StandardCharsets.US_ASCII);
        return "GIF87a".equals(signature) || "GIF89a".equals(signature);
    }

    private boolean isWebp(byte[] header) {
        if (header.length < 12) {
            return false;
        }

        String riff = new String(header, 0, 4, StandardCharsets.US_ASCII);
        String webp = new String(header, 8, 4, StandardCharsets.US_ASCII);

        return "RIFF".equals(riff) && "WEBP".equals(webp);
    }

    /** HEIC/HEIF stills use the same ISO-BMFF "ftyp" box as MP4, just with a heic-family brand. */
    private boolean isHeic(byte[] header) {
        if (header.length < 12) {
            return false;
        }

        String boxType = new String(header, 4, 4, StandardCharsets.US_ASCII);
        if (!"ftyp".equals(boxType)) {
            return false;
        }

        String brand = new String(header, 8, 4, StandardCharsets.US_ASCII).trim().toLowerCase(Locale.ROOT);
        return HEIC_FTYP_BRANDS.contains(brand);
    }

    private boolean isMp4(byte[] header) {
        if (header.length < 12) {
            return false;
        }

        String boxType = new String(header, 4, 4, StandardCharsets.US_ASCII);
        if (!"ftyp".equals(boxType)) {
            return false;
        }

        String brand = new String(header, 8, 4, StandardCharsets.US_ASCII);
        return Set.of("isom", "iso2", "mp41", "mp42", "avc1", "M4V ", "M4A ").contains(brand);
    }

    private boolean isQuickTime(byte[] header) {
        if (header.length < 12) {
            return false;
        }

        String boxType = new String(header, 4, 4, StandardCharsets.US_ASCII);
        if (!"ftyp".equals(boxType)) {
            return false;
        }

        String brand = new String(header, 8, 4, StandardCharsets.US_ASCII);
        return "qt  ".equals(brand);
    }

    private boolean isWebm(byte[] header) {
        return header.length >= 4
                && (header[0] & 0xFF) == 0x1A
                && (header[1] & 0xFF) == 0x45
                && (header[2] & 0xFF) == 0xDF
                && (header[3] & 0xFF) == 0xA3;
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

        if (fileName.isBlank()) {
            return "file";
        }

        return fileName.replaceAll("[\\r\\n]", "_");
    }

    private String normalizeMimeType(String mimeType) {
        return mimeType == null ? "" : mimeType.trim().toLowerCase(Locale.ROOT);
    }
}
