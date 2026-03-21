package com.ideiasmidias.adminuser.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAdminUserStatusRequest {

    @NotNull(message = "isActive is required")
    private Boolean isActive;
}