package com.ideiasmidias.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PortfolioProjectRequest {

    @NotNull(message = "Section id is required")
    private Long sectionId;

    @NotBlank(message = "Portuguese project title is required")
    @Size(max = 255, message = "Portuguese project title must not exceed 255 characters")
    private String titlePt;

    @NotBlank(message = "English project title is required")
    @Size(max = 255, message = "English project title must not exceed 255 characters")
    private String titleEn;

    private String shortDescriptionPt;
    private String shortDescriptionEn;
    private String fullDescriptionPt;
    private String fullDescriptionEn;

    @Size(max = 255, message = "Client name must not exceed 255 characters")
    private String clientName;

    private LocalDate projectDate;

    @Size(max = 255, message = "Portuguese location must not exceed 255 characters")
    private String locationPt;

    @Size(max = 255, message = "English location must not exceed 255 characters")
    private String locationEn;

    private String coverImageUrl;
    private String videoUrl;
    private String attributesJson;
    private Boolean isFeatured = false;
    private Boolean isActive = true;
    private Integer sortOrder = 0;
}
