package com.ideiasmidias.settings.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.settings.dto.SiteSettingsRequest;
import com.ideiasmidias.settings.dto.SiteSettingsResponse;
import com.ideiasmidias.settings.service.SiteSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/site-settings")
@RequiredArgsConstructor
public class SiteSettingsController {

    private final SiteSettingsService siteSettingsService;

    @GetMapping
    public ResponseEntity<ApiResponse<SiteSettingsResponse>> getSiteSettings() {
        SiteSettingsResponse response = siteSettingsService.getSiteSettings();

        return ResponseEntity.ok(
                ApiResponse.<SiteSettingsResponse>builder()
                        .success(true)
                        .message("Site settings fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<SiteSettingsResponse>> saveOrUpdate(@Valid @RequestBody SiteSettingsRequest request) {
        SiteSettingsResponse response = siteSettingsService.saveOrUpdate(request);

        return ResponseEntity.ok(
                ApiResponse.<SiteSettingsResponse>builder()
                        .success(true)
                        .message("Site settings saved successfully")
                        .data(response)
                        .build()
        );
    }
}