package com.ideiasmidias.sectionattribute.dto;

import com.ideiasmidias.common.enums.AttributeFieldType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SectionAttributeDefinitionResponse {

    private Long id;
    private Long sectionId;
    private String code;
    private String labelPt;
    private String labelEn;
    private AttributeFieldType fieldType;
    private Boolean isRequired;
    private Boolean isFilterable;
    private Boolean isDisplayedInCard;
    private Boolean isDisplayedInDetails;
    private String optionsJson;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
