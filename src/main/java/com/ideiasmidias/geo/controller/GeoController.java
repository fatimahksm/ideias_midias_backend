package com.ideiasmidias.geo.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.geo.dto.ResolveMapsLinkRequest;
import com.ideiasmidias.geo.dto.ResolveMapsLinkResponse;
import com.ideiasmidias.geo.service.GoogleMapsLinkResolver;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/geo")
@RequiredArgsConstructor
public class GeoController {

    private final GoogleMapsLinkResolver googleMapsLinkResolver;

    @PostMapping("/resolve-maps-link")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ResolveMapsLinkResponse>> resolveMapsLink(
            @Valid @RequestBody ResolveMapsLinkRequest request
    ) {
        ResolveMapsLinkResponse response = googleMapsLinkResolver.resolve(request.getUrl());

        return ResponseEntity.ok(
                ApiResponse.<ResolveMapsLinkResponse>builder()
                        .success(true)
                        .message("Location resolved")
                        .data(response)
                        .build()
        );
    }
}
