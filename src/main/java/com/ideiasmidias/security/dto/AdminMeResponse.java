package com.ideiasmidias.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminMeResponse {

    private Long adminId;
    private String fullName;
    private String email;
    private String role;
    private Boolean isActive;
}