package com.ideiasmidias.category.controller;

import com.ideiasmidias.category.dto.SectionCategoryRequest;
import com.ideiasmidias.category.dto.SectionCategoryResponse;
import com.ideiasmidias.category.service.SectionCategoryService;
import com.ideiasmidias.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class SectionCategoryController {

    private final SectionCategoryService sectionCategoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionCategoryResponse>> create(@Valid @RequestBody SectionCategoryRequest request) {
        SectionCategoryResponse response = sectionCategoryService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SectionCategoryResponse>builder()
                        .success(true)
                        .message("Section category created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionCategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SectionCategoryRequest request
    ) {
        SectionCategoryResponse response = sectionCategoryService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<SectionCategoryResponse>builder()
                        .success(true)
                        .message("Section category updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionCategoryResponse>> getById(@PathVariable Long id) {
        SectionCategoryResponse response = sectionCategoryService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<SectionCategoryResponse>builder()
                        .success(true)
                        .message("Section category fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionCategoryResponse>>> getAll() {
        List<SectionCategoryResponse> response = sectionCategoryService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<SectionCategoryResponse>>builder()
                        .success(true)
                        .message("Section categories fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionCategoryResponse>>> getAllActive() {
        List<SectionCategoryResponse> response = sectionCategoryService.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<SectionCategoryResponse>>builder()
                        .success(true)
                        .message("Active section categories fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionCategoryResponse>>> getBySection(@PathVariable Long sectionId) {
        List<SectionCategoryResponse> response = sectionCategoryService.getBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionCategoryResponse>>builder()
                        .success(true)
                        .message("Section categories by section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionCategoryResponse>>> getActiveBySection(@PathVariable Long sectionId) {
        List<SectionCategoryResponse> response = sectionCategoryService.getActiveBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionCategoryResponse>>builder()
                        .success(true)
                        .message("Active section categories by section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        sectionCategoryService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Section category deleted successfully")
                        .data(null)
                        .build()
        );
    }
}