package com.ideiasmidias.publicapi.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.section.dto.SectionDetailsResponse;
import com.ideiasmidias.section.dto.SectionResponse;
import com.ideiasmidias.section.service.SectionDetailsService;
import com.ideiasmidias.section.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/sections")
@RequiredArgsConstructor
public class PublicSectionController {

    private final SectionService sectionService;
    private final SectionDetailsService sectionDetailsService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getAllActiveSections() {
        List<SectionResponse> response = sectionService.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<SectionResponse>>builder()
                        .success(true)
                        .message("Public active sections fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<SectionResponse>> getActiveSectionBySlug(@PathVariable String slug) {
        SectionResponse response = sectionService.getActiveBySlug(slug);

        return ResponseEntity.ok(
                ApiResponse.<SectionResponse>builder()
                        .success(true)
                        .message("Public active section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/by-slug/{slug}/details")
    public ResponseEntity<ApiResponse<SectionDetailsResponse>> getActiveSectionDetailsBySlug(@PathVariable String slug) {
        SectionDetailsResponse response = sectionDetailsService.getPublicDetailsBySlug(slug);

        return ResponseEntity.ok(
                ApiResponse.<SectionDetailsResponse>builder()
                        .success(true)
                        .message("Public active section details fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{slug}/details")
    public ResponseEntity<ApiResponse<SectionDetailsResponse>> getActiveSectionDetailsBySlugV2(@PathVariable String slug) {
        SectionDetailsResponse response = sectionDetailsService.getPublicDetailsBySlug(slug);

        return ResponseEntity.ok(
                ApiResponse.<SectionDetailsResponse>builder()
                        .success(true)
                        .message("Public active section details fetched successfully")
                        .data(response)
                        .build()
        );
    }
}
