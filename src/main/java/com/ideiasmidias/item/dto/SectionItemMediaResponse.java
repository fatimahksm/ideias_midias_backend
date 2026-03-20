package com.ideiasmidias.item.dto;

import com.ideiasmidias.common.enums.MediaType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SectionItemMediaResponse {

    private Long id;
    private Long itemId;
    private MediaType mediaType;
    private String mediaUrl;
    private String thumbnailUrl;
    private String altTextPt;
    private String altTextEn;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}