package com.ideiasmidias.contact.dto;

import com.ideiasmidias.common.enums.ContactMethodType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactMethodRequest {

    @NotNull(message = "Contact method type is required")
    private ContactMethodType type;

    @Size(max = 150, message = "Portuguese label must not exceed 150 characters")
    private String labelPt;

    @Size(max = 150, message = "English label must not exceed 150 characters")
    private String labelEn;

    @NotBlank(message = "Contact value is required")
    @Size(max = 500, message = "Contact value must not exceed 500 characters")
    private String value;

    @Size(max = 100, message = "Icon name must not exceed 100 characters")
    private String iconName;

    private Boolean isActive = true;
    private Integer sortOrder = 0;
}