package com.ideiasmidias.settings.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThemeSettingsRequest {

    @NotBlank(message = "Primary color is required")
    @Size(max = 20, message = "Primary color must not exceed 20 characters")
    private String primaryColor;

    @NotBlank(message = "Secondary color is required")
    @Size(max = 20, message = "Secondary color must not exceed 20 characters")
    private String secondaryColor;

    @NotBlank(message = "Accent color is required")
    @Size(max = 20, message = "Accent color must not exceed 20 characters")
    private String accentColor;

    @NotBlank(message = "Background color is required")
    @Size(max = 20, message = "Background color must not exceed 20 characters")
    private String backgroundColor;

    @NotBlank(message = "Text color is required")
    @Size(max = 20, message = "Text color must not exceed 20 characters")
    private String textColor;

    @NotBlank(message = "Hero overlay color is required")
    @Size(max = 20, message = "Hero overlay color must not exceed 20 characters")
    private String heroOverlayColor;
}