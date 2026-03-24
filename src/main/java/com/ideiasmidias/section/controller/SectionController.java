package com.ideiasmidias.section.controller;

import com.ideiasmidias.common.enums.SectionType;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.section.dto.SectionDetailsResponse;
import com.ideiasmidias.section.dto.SectionRequest;
import com.ideiasmidias.section.dto.SectionResponse;
import com.ideiasmidias.section.service.SectionDetailsService;
import com.ideiasmidias.section.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;
    private final SectionDetailsService sectionDetailsService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionResponse>> create(@Valid @RequestBody SectionRequest request) {
        SectionResponse response = sectionService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SectionResponse>builder()
                        .success(true)
                        .message("Section created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SectionRequest request
    ) {
        SectionResponse response = sectionService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<SectionResponse>builder()
                        .success(true)
                        .message("Section updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionResponse>> getById(@PathVariable Long id) {
        SectionResponse response = sectionService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<SectionResponse>builder()
                        .success(true)
                        .message("Section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}/details")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionDetailsResponse>> getDetailsById(@PathVariable Long id) {
        SectionDetailsResponse response = sectionDetailsService.getAdminDetailsById(id);

        return ResponseEntity.ok(
                ApiResponse.<SectionDetailsResponse>builder()
                        .success(true)
                        .message("Section details fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/slug/{slug}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionResponse>> getBySlug(@PathVariable String slug) {
        SectionResponse response = sectionService.getBySlug(slug);

        return ResponseEntity.ok(
                ApiResponse.<SectionResponse>builder()
                        .success(true)
                        .message("Section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/slug/{slug}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionResponse>> getActiveBySlug(@PathVariable String slug) {
        SectionResponse response = sectionService.getActiveBySlug(slug);

        return ResponseEntity.ok(
                ApiResponse.<SectionResponse>builder()
                        .success(true)
                        .message("Active section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getAll() {
        List<SectionResponse> response = sectionService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<SectionResponse>>builder()
                        .success(true)
                        .message("Sections fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getAllActive() {
        List<SectionResponse> response = sectionService.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<SectionResponse>>builder()
                        .success(true)
                        .message("Active sections fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/type/{sectionType}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getByType(@PathVariable SectionType sectionType) {
        List<SectionResponse> response = sectionService.getByType(sectionType);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionResponse>>builder()
                        .success(true)
                        .message("Sections by type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/type/{sectionType}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getActiveByType(@PathVariable SectionType sectionType) {
        List<SectionResponse> response = sectionService.getActiveByType(sectionType);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionResponse>>builder()
                        .success(true)
                        .message("Active sections by type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        sectionService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Section deleted successfully")
                        .data(null)
                        .build()
        );
    }
}
