package com.ideiasmidias.media.service;

import com.ideiasmidias.common.enums.MediaType;

public record StoredMediaFile(
        String storedFileName,
        String originalName,
        String fileUrl,
        MediaType fileType,
        String mimeType,
        Long fileSize
) {
}