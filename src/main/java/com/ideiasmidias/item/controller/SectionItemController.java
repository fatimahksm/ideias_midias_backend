package com.ideiasmidias.item.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.item.dto.SectionItemRequest;
import com.ideiasmidias.item.dto.SectionItemResponse;
import com.ideiasmidias.item.service.SectionItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/items")
@RequiredArgsConstructor
public class SectionItemController {

    private final SectionItemService sectionItemService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<SectionItemResponse>> create(@Valid @RequestBody SectionItemRequest request) {
        SectionItemResponse response = sectionItemService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SectionItemResponse>builder()
                        .success(true)
                        .message("Section item created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<SectionItemResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SectionItemRequest request
    ) {
        SectionItemResponse response = sectionItemService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<SectionItemResponse>builder()
                        .success(true)
                        .message("Section item updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<SectionItemResponse>> getById(@PathVariable Long id) {
        SectionItemResponse response = sectionItemService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<SectionItemResponse>builder()
                        .success(true)
                        .message("Section item fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionItemResponse>>> getAll() {
        List<SectionItemResponse> response = sectionItemService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemResponse>>builder()
                        .success(true)
                        .message("Section items fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionItemResponse>>> getAllActive() {
        List<SectionItemResponse> response = sectionItemService.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemResponse>>builder()
                        .success(true)
                        .message("Active section items fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionItemResponse>>> getBySection(@PathVariable Long sectionId) {
        List<SectionItemResponse> response = sectionItemService.getBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemResponse>>builder()
                        .success(true)
                        .message("Section items by section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionItemResponse>>> getActiveBySection(@PathVariable Long sectionId) {
        List<SectionItemResponse> response = sectionItemService.getActiveBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemResponse>>builder()
                        .success(true)
                        .message("Active section items by section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionItemResponse>>> getByCategory(@PathVariable Long categoryId) {
        List<SectionItemResponse> response = sectionItemService.getByCategory(categoryId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemResponse>>builder()
                        .success(true)
                        .message("Section items by category fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/category/{categoryId}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionItemResponse>>> getActiveByCategory(@PathVariable Long categoryId) {
        List<SectionItemResponse> response = sectionItemService.getActiveByCategory(categoryId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemResponse>>builder()
                        .success(true)
                        .message("Active section items by category fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/direct")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionItemResponse>>> getDirectItemsBySection(@PathVariable Long sectionId) {
        List<SectionItemResponse> response = sectionItemService.getDirectItemsBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemResponse>>builder()
                        .success(true)
                        .message("Direct section items fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/direct/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionItemResponse>>> getActiveDirectItemsBySection(@PathVariable Long sectionId) {
        List<SectionItemResponse> response = sectionItemService.getActiveDirectItemsBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemResponse>>builder()
                        .success(true)
                        .message("Active direct section items fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/featured")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionItemResponse>>> getFeaturedBySection(@PathVariable Long sectionId) {
        List<SectionItemResponse> response = sectionItemService.getFeaturedBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemResponse>>builder()
                        .success(true)
                        .message("Featured section items fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/featured/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<SectionItemResponse>>> getActiveFeaturedBySection(@PathVariable Long sectionId) {
        List<SectionItemResponse> response = sectionItemService.getActiveFeaturedBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemResponse>>builder()
                        .success(true)
                        .message("Active featured section items fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        sectionItemService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Section item deleted successfully")
                        .data(null)
                        .build()
        );
    }
}