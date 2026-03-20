package com.ideiasmidias.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionCategoryRequest {

    @NotNull(message = "Section id is required")
    private Long sectionId;

    @NotBlank(message = "Portuguese category name is required")
    @Size(max = 255, message = "Portuguese category name must not exceed 255 characters")
    private String namePt;

    @NotBlank(message = "English category name is required")
    @Size(max = 255, message = "English category name must not exceed 255 characters")
    private String nameEn;

    private String descriptionPt;
    private String descriptionEn;
    private String imageUrl;

    private Boolean isActive = true;
    private Integer sortOrder = 0;
}