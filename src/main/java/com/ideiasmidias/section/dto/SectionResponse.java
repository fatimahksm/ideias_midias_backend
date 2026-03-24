package com.ideiasmidias.section.dto;

import com.ideiasmidias.common.enums.SectionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SectionResponse {

    private Long id;
    private String slug;
    private String namePt;
    private String nameEn;
    private String descriptionPt;
    private String descriptionEn;
    private SectionType sectionType;
    private String coverImageUrl;
    private String coverVideoUrl;
    private String displayVariant;
    private String layoutStyle;
    private Boolean showIntro;
    private Boolean showGallery;
    private Boolean showFilters;
    private Boolean showItemDetails;
    private String detailsViewMode;
    private Boolean allowCustomAttributes;
    private String settingsJson;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
