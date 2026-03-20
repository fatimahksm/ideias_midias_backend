package com.ideiasmidias.homecard.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.homecard.dto.HomeCardRequest;
import com.ideiasmidias.homecard.dto.HomeCardResponse;
import com.ideiasmidias.homecard.service.HomeCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/home-cards")
@RequiredArgsConstructor
public class HomeCardController {

    private final HomeCardService homeCardService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<HomeCardResponse>> create(@Valid @RequestBody HomeCardRequest request) {
        HomeCardResponse response = homeCardService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<HomeCardResponse>builder()
                        .success(true)
                        .message("Home card created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<HomeCardResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody HomeCardRequest request
    ) {
        HomeCardResponse response = homeCardService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<HomeCardResponse>builder()
                        .success(true)
                        .message("Home card updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<HomeCardResponse>> getById(@PathVariable Long id) {
        HomeCardResponse response = homeCardService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<HomeCardResponse>builder()
                        .success(true)
                        .message("Home card fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<HomeCardResponse>>> getAll() {
        List<HomeCardResponse> response = homeCardService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<HomeCardResponse>>builder()
                        .success(true)
                        .message("Home cards fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<HomeCardResponse>>> getAllActive() {
        List<HomeCardResponse> response = homeCardService.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<HomeCardResponse>>builder()
                        .success(true)
                        .message("Active home cards fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<HomeCardResponse>>> getBySection(@PathVariable Long sectionId) {
        List<HomeCardResponse> response = homeCardService.getBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<HomeCardResponse>>builder()
                        .success(true)
                        .message("Home cards by section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<HomeCardResponse>>> getActiveBySection(@PathVariable Long sectionId) {
        List<HomeCardResponse> response = homeCardService.getActiveBySection(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<List<HomeCardResponse>>builder()
                        .success(true)
                        .message("Active home cards by section fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        homeCardService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Home card deleted successfully")
                        .data(null)
                        .build()
        );
    }
}