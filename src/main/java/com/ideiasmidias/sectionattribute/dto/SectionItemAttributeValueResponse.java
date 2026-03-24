package com.ideiasmidias.sectionattribute.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class SectionItemAttributeValueResponse {

    private Long id;
    private Long attributeDefinitionId;
    private String attributeCode;
    private String labelPt;
    private String labelEn;
    private String valueText;
    private Double valueNumber;
    private LocalDate valueDate;
    private Boolean valueBoolean;
}
