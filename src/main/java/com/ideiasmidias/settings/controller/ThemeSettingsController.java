package com.ideiasmidias.settings.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.settings.dto.ThemeSettingsRequest;
import com.ideiasmidias.settings.dto.ThemeSettingsResponse;
import com.ideiasmidias.settings.service.ThemeSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/theme-settings")
@RequiredArgsConstructor
public class ThemeSettingsController {

    private final ThemeSettingsService themeSettingsService;

    @GetMapping
    public ResponseEntity<ApiResponse<ThemeSettingsResponse>> getThemeSettings() {
        ThemeSettingsResponse response = themeSettingsService.getThemeSettings();

        return ResponseEntity.ok(
                ApiResponse.<ThemeSettingsResponse>builder()
                        .success(true)
                        .message("Theme settings fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ThemeSettingsResponse>> saveOrUpdate(@Valid @RequestBody ThemeSettingsRequest request) {
        ThemeSettingsResponse response = themeSettingsService.saveOrUpdate(request);

        return ResponseEntity.ok(
                ApiResponse.<ThemeSettingsResponse>builder()
                        .success(true)
                        .message("Theme settings saved successfully")
                        .data(response)
                        .build()
        );
    }
}