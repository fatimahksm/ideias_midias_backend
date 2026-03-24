package com.ideiasmidias.sectionattribute.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.sectionattribute.dto.SectionAttributeDefinitionRequest;
import com.ideiasmidias.sectionattribute.dto.SectionAttributeDefinitionResponse;
import com.ideiasmidias.sectionattribute.service.SectionAttributeDefinitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/section-attributes")
@RequiredArgsConstructor
public class SectionAttributeDefinitionController {

    private final SectionAttributeDefinitionService sectionAttributeDefinitionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionAttributeDefinitionResponse>> create(@Valid @RequestBody SectionAttributeDefinitionRequest request) {
        SectionAttributeDefinitionResponse response = sectionAttributeDefinitionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SectionAttributeDefinitionResponse>builder()
                        .success(true)
                        .message("Section attribute definition created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionAttributeDefinitionResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SectionAttributeDefinitionRequest request
    ) {
        SectionAttributeDefinitionResponse response = sectionAttributeDefinitionService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.<SectionAttributeDefinitionResponse>builder()
                        .success(true)
                        .message("Section attribute definition updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionAttributeDefinitionResponse>> getById(@PathVariable Long id) {
        SectionAttributeDefinitionResponse response = sectionAttributeDefinitionService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.<SectionAttributeDefinitionResponse>builder()
                        .success(true)
                        .message("Section attribute definition fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionAttributeDefinitionResponse>>> getBySection(@PathVariable Long sectionId) {
        List<SectionAttributeDefinitionResponse> response = sectionAttributeDefinitionService.getBySection(sectionId, false);
        return ResponseEntity.ok(
                ApiResponse.<List<SectionAttributeDefinitionResponse>>builder()
                        .success(true)
                        .message("Section attribute definitions fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        sectionAttributeDefinitionService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Section attribute definition deleted successfully")
                        .data(null)
                        .build()
        );
    }
}
