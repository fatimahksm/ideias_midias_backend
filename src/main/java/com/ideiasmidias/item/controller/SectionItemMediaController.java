package com.ideiasmidias.item.controller;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.item.dto.SectionItemMediaRequest;
import com.ideiasmidias.item.dto.SectionItemMediaResponse;
import com.ideiasmidias.item.service.SectionItemMediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/item-media")
@RequiredArgsConstructor
public class SectionItemMediaController {

    private final SectionItemMediaService sectionItemMediaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionItemMediaResponse>> create(@Valid @RequestBody SectionItemMediaRequest request) {
        SectionItemMediaResponse response = sectionItemMediaService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SectionItemMediaResponse>builder()
                        .success(true)
                        .message("Section item media created successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionItemMediaResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SectionItemMediaRequest request
    ) {
        SectionItemMediaResponse response = sectionItemMediaService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<SectionItemMediaResponse>builder()
                        .success(true)
                        .message("Section item media updated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SectionItemMediaResponse>> getById(@PathVariable Long id) {
        SectionItemMediaResponse response = sectionItemMediaService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<SectionItemMediaResponse>builder()
                        .success(true)
                        .message("Section item media fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionItemMediaResponse>>> getAll() {
        List<SectionItemMediaResponse> response = sectionItemMediaService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemMediaResponse>>builder()
                        .success(true)
                        .message("Section item media list fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionItemMediaResponse>>> getAllActive() {
        List<SectionItemMediaResponse> response = sectionItemMediaService.getAllActive();

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemMediaResponse>>builder()
                        .success(true)
                        .message("Active section item media list fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/item/{itemId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionItemMediaResponse>>> getByItem(@PathVariable Long itemId) {
        List<SectionItemMediaResponse> response = sectionItemMediaService.getByItem(itemId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemMediaResponse>>builder()
                        .success(true)
                        .message("Section item media by item fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/item/{itemId}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionItemMediaResponse>>> getActiveByItem(@PathVariable Long itemId) {
        List<SectionItemMediaResponse> response = sectionItemMediaService.getActiveByItem(itemId);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemMediaResponse>>builder()
                        .success(true)
                        .message("Active section item media by item fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/item/{itemId}/type/{mediaType}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionItemMediaResponse>>> getByItemAndType(
            @PathVariable Long itemId,
            @PathVariable MediaType mediaType
    ) {
        List<SectionItemMediaResponse> response = sectionItemMediaService.getByItemAndType(itemId, mediaType);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemMediaResponse>>builder()
                        .success(true)
                        .message("Section item media by item and type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/item/{itemId}/type/{mediaType}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SectionItemMediaResponse>>> getActiveByItemAndType(
            @PathVariable Long itemId,
            @PathVariable MediaType mediaType
    ) {
        List<SectionItemMediaResponse> response = sectionItemMediaService.getActiveByItemAndType(itemId, mediaType);

        return ResponseEntity.ok(
                ApiResponse.<List<SectionItemMediaResponse>>builder()
                        .success(true)
                        .message("Active section item media by item and type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        sectionItemMediaService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Section item media deleted successfully")
                        .data(null)
                        .build()
        );
    }
}