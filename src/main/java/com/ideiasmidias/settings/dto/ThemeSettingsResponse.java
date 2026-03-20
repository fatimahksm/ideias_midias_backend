package com.ideiasmidias.settings.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ThemeSettingsResponse {

    private Long id;
    private String primaryColor;
    private String secondaryColor;
    private String accentColor;
    private String backgroundColor;
    private String textColor;
    private String heroOverlayColor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}