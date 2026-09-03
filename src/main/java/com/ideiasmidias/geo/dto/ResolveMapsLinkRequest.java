package com.ideiasmidias.geo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResolveMapsLinkRequest {

    @NotBlank(message = "Link is required")
    private String url;
}
