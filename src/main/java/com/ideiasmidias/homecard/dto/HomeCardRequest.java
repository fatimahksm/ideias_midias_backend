package com.ideiasmidias.homecard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeCardRequest {

    @NotNull(message = "Section id is required")
    private Long sectionId;

    @NotBlank(message = "Portuguese title is required")
    @Size(max = 255, message = "Portuguese title must not exceed 255 characters")
    private String titlePt;

    @NotBlank(message = "English title is required")
    @Size(max = 255, message = "English title must not exceed 255 characters")
    private String titleEn;

    private String shortDescriptionPt;
    private String shortDescriptionEn;

    private String imageUrl;

    @Size(max = 100, message = "Icon name must not exceed 100 characters")
    private String iconName;

    private Boolean isActive = true;
    private Integer sortOrder = 0;
}