package com.ideiasmidias.homecard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class HomeCardResponse {

    private Long id;
    private Long sectionId;
    private String titlePt;
    private String titleEn;
    private String shortDescriptionPt;
    private String shortDescriptionEn;
    private String imageUrl;
    private String iconName;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}