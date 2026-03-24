package com.ideiasmidias.sectionattribute.dto;

import com.ideiasmidias.common.enums.AttributeFieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionAttributeDefinitionRequest {

    @NotNull(message = "Section id is required")
    private Long sectionId;

    @NotBlank(message = "Attribute code is required")
    @Size(max = 120, message = "Attribute code must not exceed 120 characters")
    private String code;

    @NotBlank(message = "Portuguese label is required")
    @Size(max = 255, message = "Portuguese label must not exceed 255 characters")
    private String labelPt;

    @NotBlank(message = "English label is required")
    @Size(max = 255, message = "English label must not exceed 255 characters")
    private String labelEn;

    @NotNull(message = "Field type is required")
    private AttributeFieldType fieldType;

    private Boolean isRequired = false;
    private Boolean isFilterable = false;
    private Boolean isDisplayedInCard = true;
    private Boolean isDisplayedInDetails = true;
    private String optionsJson;
    private Boolean isActive = true;
    private Integer sortOrder = 0;
}
