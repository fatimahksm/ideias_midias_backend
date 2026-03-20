package com.ideiasmidias.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SectionItemResponse {

    private Long id;
    private Long sectionId;
    private Long categoryId;
    private String titlePt;
    private String titleEn;
    private String shortDescriptionPt;
    private String shortDescriptionEn;
    private String fullDescriptionPt;
    private String fullDescriptionEn;
    private String coverImageUrl;
    private String videoUrl;
    private String itemType;
    private String specificationsPt;
    private String specificationsEn;
    private Boolean isFeatured;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}