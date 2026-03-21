package com.ideiasmidias.portfolio.controller;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.portfolio.dto.PortfolioProjectMediaRequest;
import com.ideiasmidias.portfolio.dto.PortfolioProjectMediaResponse;
import com.ideiasmidias.portfolio.service.PortfolioProjectMediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/portfolio-media")
@RequiredArgsConstructor
public class PortfolioProjectMediaController {

    private final PortfolioProjectMediaService portfolioProjectMediaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<PortfolioProjectMediaResponse>> create(@Valid @RequestBody PortfolioProjectMediaRequest request) {
        PortfolioProjectMediaResponse response = portfolioProjectMediaService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PortfolioProjectMediaResponse>builder()
                        .success(true)
                        .message("Portfolio project media created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<PortfolioProjectMediaResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody PortfolioProjectMediaRequest request
    ) {
        PortfolioProjectMediaResponse response = portfolioProjectMediaService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<PortfolioProjectMediaResponse>builder()
                        .success(true)
                        .message("Portfolio project media updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<PortfolioProjectMediaResponse>> getById(@PathVariable Long id) {
        PortfolioProjectMediaResponse response = portfolioProjectMediaService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<PortfolioProjectMediaResponse>builder()
                        .success(true)
                        .message("Portfolio project media fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PortfolioProjectMediaResponse>>> getAll() {
        List<PortfolioProjectMediaResponse> response = portfolioProjectMediaService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectMediaResponse>>builder()
                        .success(true)
                        .message("Portfolio project media list fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PortfolioProjectMediaResponse>>> getAllActive() {
        List<PortfolioProjectMediaResponse> response = portfolioProjectMediaService.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectMediaResponse>>builder()
                        .success(true)
                        .message("Active portfolio project media list fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PortfolioProjectMediaResponse>>> getByProject(@PathVariable Long projectId) {
        List<PortfolioProjectMediaResponse> response = portfolioProjectMediaService.getByProject(projectId);

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectMediaResponse>>builder()
                        .success(true)
                        .message("Portfolio project media by project fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/project/{projectId}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PortfolioProjectMediaResponse>>> getActiveByProject(@PathVariable Long projectId) {
        List<PortfolioProjectMediaResponse> response = portfolioProjectMediaService.getActiveByProject(projectId);

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectMediaResponse>>builder()
                        .success(true)
                        .message("Active portfolio project media by project fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/project/{projectId}/type/{mediaType}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PortfolioProjectMediaResponse>>> getByProjectAndType(
            @PathVariable Long projectId,
            @PathVariable MediaType mediaType
    ) {
        List<PortfolioProjectMediaResponse> response = portfolioProjectMediaService.getByProjectAndType(projectId, mediaType);

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectMediaResponse>>builder()
                        .success(true)
                        .message("Portfolio project media by project and type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/project/{projectId}/type/{mediaType}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PortfolioProjectMediaResponse>>> getActiveByProjectAndType(
            @PathVariable Long projectId,
            @PathVariable MediaType mediaType
    ) {
        List<PortfolioProjectMediaResponse> response = portfolioProjectMediaService.getActiveByProjectAndType(projectId, mediaType);

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectMediaResponse>>builder()
                        .success(true)
                        .message("Active portfolio project media by project and type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        portfolioProjectMediaService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Portfolio project media deleted successfully")
                        .data(null)
                        .build()
        );
    }
}