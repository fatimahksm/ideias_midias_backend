package com.ideiasmidias.portfolio.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.common.response.PageResponse;
import com.ideiasmidias.portfolio.dto.PortfolioProjectRequest;
import com.ideiasmidias.portfolio.dto.PortfolioProjectResponse;
import com.ideiasmidias.portfolio.dto.PortfolioProjectStatsResponse;
import com.ideiasmidias.portfolio.service.PortfolioProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/portfolio-projects")
@RequiredArgsConstructor
public class PortfolioProjectController {

    private final PortfolioProjectService portfolioProjectService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<PortfolioProjectResponse>> create(@Valid @RequestBody PortfolioProjectRequest request) {
        PortfolioProjectResponse response = portfolioProjectService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PortfolioProjectResponse>builder()
                        .success(true)
                        .message("Portfolio project created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<PortfolioProjectResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody PortfolioProjectRequest request
    ) {
        PortfolioProjectResponse response = portfolioProjectService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<PortfolioProjectResponse>builder()
                        .success(true)
                        .message("Portfolio project updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<PortfolioProjectResponse>> getById(@PathVariable Long id) {
        PortfolioProjectResponse response = portfolioProjectService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<PortfolioProjectResponse>builder()
                        .success(true)
                        .message("Portfolio project fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PortfolioProjectResponse>>> getAll() {
        List<PortfolioProjectResponse> response = portfolioProjectService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectResponse>>builder()
                        .success(true)
                        .message("Portfolio projects fetched successfully")
                        .data(response)
                        .build()
        );
    }

    /** Filtered, sorted page of projects for the admin listing. */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<PortfolioProjectStatsResponse>> stats(
            @RequestParam(required = false) Long sectionId
    ) {
        return ResponseEntity.ok(
                ApiResponse.<PortfolioProjectStatsResponse>builder()
                        .success(true)
                        .message("Portfolio project stats fetched successfully")
                        .data(portfolioProjectService.stats(sectionId))
                        .build()
        );
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<PortfolioProjectResponse>>> search(
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size
    ) {
        PageResponse<PortfolioProjectResponse> response =
                portfolioProjectService.search(sectionId, status, featured, search, sort, page, size);

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<PortfolioProjectResponse>>builder()
                        .success(true)
                        .message("Portfolio projects page fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PortfolioProjectResponse>>> getAllActive() {
        List<PortfolioProjectResponse> response = portfolioProjectService.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectResponse>>builder()
                        .success(true)
                        .message("Active portfolio projects fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PortfolioProjectResponse>>> getBySection(@PathVariable Long sectionId) {
        List<PortfolioProjectResponse> response = portfolioProjectService.getBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectResponse>>builder()
                        .success(true)
                        .message("Portfolio projects by section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PortfolioProjectResponse>>> getActiveBySection(@PathVariable Long sectionId) {
        List<PortfolioProjectResponse> response = portfolioProjectService.getActiveBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectResponse>>builder()
                        .success(true)
                        .message("Active portfolio projects by section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/featured")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PortfolioProjectResponse>>> getFeaturedBySection(@PathVariable Long sectionId) {
        List<PortfolioProjectResponse> response = portfolioProjectService.getFeaturedBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectResponse>>builder()
                        .success(true)
                        .message("Featured portfolio projects fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/featured/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PortfolioProjectResponse>>> getActiveFeaturedBySection(@PathVariable Long sectionId) {
        List<PortfolioProjectResponse> response = portfolioProjectService.getActiveFeaturedBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<PortfolioProjectResponse>>builder()
                        .success(true)
                        .message("Active featured portfolio projects fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        portfolioProjectService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Portfolio project deleted successfully")
                        .data(null)
                        .build()
        );
    }
}