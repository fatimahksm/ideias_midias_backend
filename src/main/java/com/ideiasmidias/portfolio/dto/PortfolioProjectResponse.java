package com.ideiasmidias.portfolio.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PortfolioProjectResponse {

    private Long id;
    private Long sectionId;
    private String titlePt;
    private String titleEn;
    private String shortDescriptionPt;
    private String shortDescriptionEn;
    private String fullDescriptionPt;
    private String fullDescriptionEn;
    private String clientName;
    private LocalDate projectDate;
    private String locationPt;
    private String locationEn;
    private String coverImageUrl;
    private String videoUrl;
    private String attributesJson;
    private Boolean isFeatured;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
