package com.ideiasmidias.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionItemRequest {

    @NotNull(message = "Section id is required")
    private Long sectionId;

    private Long categoryId;

    @NotBlank(message = "Portuguese item title is required")
    @Size(max = 255, message = "Portuguese item title must not exceed 255 characters")
    private String titlePt;

    @NotBlank(message = "English item title is required")
    @Size(max = 255, message = "English item title must not exceed 255 characters")
    private String titleEn;

    private String shortDescriptionPt;
    private String shortDescriptionEn;
    private String fullDescriptionPt;
    private String fullDescriptionEn;
    private String coverImageUrl;
    private String videoUrl;

    @Size(max = 120, message = "Item type must not exceed 120 characters")
    private String itemType;

    private String specificationsPt;
    private String specificationsEn;

    private Boolean isFeatured = false;
    private Boolean isActive = true;
    private Integer sortOrder = 0;
}