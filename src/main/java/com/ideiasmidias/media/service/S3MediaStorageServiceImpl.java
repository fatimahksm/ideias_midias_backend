package com.ideiasmidias.media.service;

import com.ideiasmidias.common.enums.MediaType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Stores uploads in Cloudflare R2 (or any S3-compatible bucket). Activated by
 * setting {@code app.storage.provider=r2} plus the {@code app.storage.r2.*}
 * properties below — see application-example.properties for the full list.
 *
 * R2 buckets are private by default: you need either an r2.dev public
 * development URL or a custom domain attached to the bucket, and that URL
 * goes in {@code app.storage.r2.public-base-url}.
 */
@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "r2")
public class S3MediaStorageServiceImpl implements MediaStorageService {

    private final MediaFileValidator validator;

    @Value("${app.storage.r2.account-id}")
    private String accountId;

    @Value("${app.storage.r2.access-key-id}")
    private String accessKeyId;

    @Value("${app.storage.r2.secret-access-key}")
    private String secretAccessKey;

    @Value("${app.storage.r2.bucket}")
    private String bucket;

    @Value("${app.storage.r2.public-base-url}")
    private String publicBaseUrl;

    private S3Client s3Client;

    public S3MediaStorageServiceImpl(MediaFileValidator validator) {
        this.validator = validator;
    }

    @PostConstruct
    void init() {
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1) // R2 does not use regions; the SDK just requires one to be set.
                .endpointOverride(URI.create("https://" + accountId + ".r2.cloudflarestorage.com"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }

    @Override
    public StoredMediaFile store(MultipartFile file) {
        MediaFileValidator.ValidatedFile validated = validator.validate(file);
        String key = UUID.randomUUID() + validated.extension();

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(validated.mimeType())
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException ex) {
            throw new RuntimeException("Failed to upload file to R2", ex);
        }

        return new StoredMediaFile(
                key,
                validated.originalName(),
                buildPublicUrl(key),
                validated.mediaType(),
                validated.mimeType(),
                file.getSize()
        );
    }

    @Override
    public StoredMediaFile storeGeneratedFile(Path filePath, String mimeType, MediaType mediaType) {
        String extension = mimeType.equals("video/mp4") ? ".mp4" : "";
        String key = UUID.randomUUID() + extension;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(mimeType)
                        .build(),
                RequestBody.fromFile(filePath)
        );

        long size;
        try {
            size = Files.size(filePath);
        } catch (IOException ex) {
            size = 0L;
        }

        return new StoredMediaFile(key, key, buildPublicUrl(key), mediaType, mimeType, size);
    }

    @Override
    public void deleteByFileUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        String key = extractKey(fileUrl);
        if (key == null || key.isBlank()) {
            return;
        }

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    @Override
    public InputStream openStream(String fileUrl) {
        String key = extractKey(fileUrl);

        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    private String buildPublicUrl(String key) {
        String base = publicBaseUrl.endsWith("/")
                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;

        return base + "/" + key;
    }

    private String extractKey(String fileUrl) {
        String base = publicBaseUrl.endsWith("/")
                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;

        if (fileUrl.startsWith(base + "/")) {
            return fileUrl.substring(base.length() + 1);
        }

        // Fall back to just the last path segment, in case the URL shape changed.
        int lastSlash = fileUrl.lastIndexOf('/');
        return lastSlash >= 0 ? fileUrl.substring(lastSlash + 1) : fileUrl;
    }
}
