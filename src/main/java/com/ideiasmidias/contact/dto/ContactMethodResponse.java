package com.ideiasmidias.contact.dto;

import com.ideiasmidias.common.enums.ContactMethodType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ContactMethodResponse {

    private Long id;
    private ContactMethodType type;
    private String labelPt;
    private String labelEn;
    private String value;
    private String iconName;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}