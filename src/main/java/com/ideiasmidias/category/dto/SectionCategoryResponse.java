package com.ideiasmidias.category.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SectionCategoryResponse {

    private Long id;
    private Long sectionId;
    private String namePt;
    private String nameEn;
    private String descriptionPt;
    private String descriptionEn;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}