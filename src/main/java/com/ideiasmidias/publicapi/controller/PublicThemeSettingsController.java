package com.ideiasmidias.publicapi.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.settings.dto.ThemeSettingsResponse;
import com.ideiasmidias.settings.service.ThemeSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/theme-settings")
@RequiredArgsConstructor
public class PublicThemeSettingsController {

    private final ThemeSettingsService themeSettingsService;

    @GetMapping
    public ResponseEntity<ApiResponse<ThemeSettingsResponse>> getThemeSettings() {
        ThemeSettingsResponse response = themeSettingsService.getThemeSettings();

        return ResponseEntity.ok(
                ApiResponse.<ThemeSettingsResponse>builder()
                        .success(true)
                        .message("Public theme settings fetched successfully")
                        .data(response)
                        .build()
        );
    }
}