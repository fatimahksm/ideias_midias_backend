package com.ideiasmidias.publicapi.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.settings.dto.SiteSettingsResponse;
import com.ideiasmidias.settings.service.SiteSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/site-settings")
@RequiredArgsConstructor
public class PublicSiteSettingsController {

    private final SiteSettingsService siteSettingsService;

    @GetMapping
    public ResponseEntity<ApiResponse<SiteSettingsResponse>> getSiteSettings() {
        SiteSettingsResponse response = siteSettingsService.getSiteSettings();

        return ResponseEntity.ok(
                ApiResponse.<SiteSettingsResponse>builder()
                        .success(true)
                        .message("Public site settings fetched successfully")
                        .data(response)
                        .build()
        );
    }
}