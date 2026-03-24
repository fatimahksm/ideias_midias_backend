package com.ideiasmidias.sectionattribute.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SectionItemAttributeValueRequest {

    @NotNull(message = "Attribute definition id is required")
    private Long attributeDefinitionId;

    private String valueText;
    private Double valueNumber;
    private LocalDate valueDate;
    private Boolean valueBoolean;
}
