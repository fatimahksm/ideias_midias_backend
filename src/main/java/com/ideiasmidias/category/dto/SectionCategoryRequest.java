package com.ideiasmidias.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionCategoryRequest {

    @NotNull
    private Long sectionId;

    @NotBlank
    @Size(max = 255)
    private String namePt;

    @NotBlank
    @Size(max = 255)
    private String nameEn;

    private String descriptionPt;

    private String descriptionEn;

    private Boolean isActive;

    private Integer sortOrder;
}