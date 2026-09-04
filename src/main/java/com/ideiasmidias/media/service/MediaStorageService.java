package com.ideiasmidias.media.service;

import com.ideiasmidias.common.enums.MediaType;

import java.io.InputStream;
import java.nio.file.Path;

public interface MediaStorageService {

    StoredMediaFile store(org.springframework.web.multipart.MultipartFile file);

    /**
     * Stores a file that was generated on the server (e.g. an ffmpeg
     * transcode output) rather than uploaded directly by a client. Skips the
     * upload-time mime/signature checks in {@link MediaFileValidator} since
     * the content was already produced by a trusted local process.
     */
    StoredMediaFile storeGeneratedFile(Path filePath, String mimeType, MediaType mediaType);

    void deleteByFileUrl(String fileUrl);

    /**
     * Reads a previously stored file back, e.g. so the admin UI can re-crop
     * an already-uploaded image without the browser fetching it directly
     * (which would need CORS access to the storage bucket).
     */
    InputStream openStream(String fileUrl);
}