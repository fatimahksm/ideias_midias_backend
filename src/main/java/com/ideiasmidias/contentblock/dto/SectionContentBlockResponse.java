package com.ideiasmidias.contentblock.dto;

import com.ideiasmidias.common.enums.ContentBlockType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SectionContentBlockResponse {

    private Long id;
    private Long sectionId;
    private ContentBlockType blockType;
    private String titlePt;
    private String titleEn;
    private String subtitlePt;
    private String subtitleEn;
    private String contentPt;
    private String contentEn;
    private String imageUrl;
    private String videoUrl;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}