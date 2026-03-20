package com.ideiasmidias.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminLoginResponse {

    private String token;
    private String tokenType;
    private Long adminId;
    private String email;
    private String role;
}