package com.ideiasmidias.contentblock.controller;

import com.ideiasmidias.common.enums.ContentBlockType;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.contentblock.dto.SectionContentBlockRequest;
import com.ideiasmidias.contentblock.dto.SectionContentBlockResponse;
import com.ideiasmidias.contentblock.service.SectionContentBlockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/content-blocks")
@RequiredArgsConstructor
public class SectionContentBlockController {

    private final SectionContentBlockService sectionContentBlockService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<SectionContentBlockResponse>> create(@Valid @RequestBody SectionContentBlockRequest request) {
        SectionContentBlockResponse response = sectionContentBlockService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SectionContentBlockResponse>builder()
                        .success(true)
                        .message("Section content block created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<SectionContentBlockResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SectionContentBlockRequest request
    ) {
        SectionContentBlockResponse response = sectionContentBlockService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<SectionContentBlockResponse>builder()
                        .success(true)
                        .message("Section content block updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<SectionContentBlockResponse>> getById(@PathVariable Long id) {
        SectionContentBlockResponse response = sectionContentBlockService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<SectionContentBlockResponse>builder()
                        .success(true)
                        .message("Section content block fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionContentBlockResponse>>> getAll() {
        List<SectionContentBlockResponse> response = sectionContentBlockService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<SectionContentBlockResponse>>builder()
                        .success(true)
                        .message("Section content blocks fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionContentBlockResponse>>> getAllActive() {
        List<SectionContentBlockResponse> response = sectionContentBlockService.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<SectionContentBlockResponse>>builder()
                        .success(true)
                        .message("Active section content blocks fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionContentBlockResponse>>> getBySection(@PathVariable Long sectionId) {
        List<SectionContentBlockResponse> response = sectionContentBlockService.getBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionContentBlockResponse>>builder()
                        .success(true)
                        .message("Section content blocks by section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionContentBlockResponse>>> getActiveBySection(@PathVariable Long sectionId) {
        List<SectionContentBlockResponse> response = sectionContentBlockService.getActiveBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionContentBlockResponse>>builder()
                        .success(true)
                        .message("Active section content blocks by section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/type/{blockType}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionContentBlockResponse>>> getBySectionAndType(
            @PathVariable Long sectionId,
            @PathVariable ContentBlockType blockType
    ) {
        List<SectionContentBlockResponse> response = sectionContentBlockService.getBySectionAndType(sectionId, blockType);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionContentBlockResponse>>builder()
                        .success(true)
                        .message("Section content blocks by section and type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/type/{blockType}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionContentBlockResponse>>> getActiveBySectionAndType(
            @PathVariable Long sectionId,
            @PathVariable ContentBlockType blockType
    ) {
        List<SectionContentBlockResponse> response = sectionContentBlockService.getActiveBySectionAndType(sectionId, blockType);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionContentBlockResponse>>builder()
                        .success(true)
                        .message("Active section content blocks by section and type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        sectionContentBlockService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Section content block deleted successfully")
                        .data(null)
                        .build()
        );
    }
}