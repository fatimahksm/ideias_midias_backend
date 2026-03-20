package com.ideiasmidias.media.dto;

import com.ideiasmidias.common.enums.MediaType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MediaLibraryResponse {

    private Long id;
    private String fileName;
    private String originalName;
    private String fileUrl;
    private MediaType fileType;
    private String mimeType;
    private Long fileSize;
    private Long uploadedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}