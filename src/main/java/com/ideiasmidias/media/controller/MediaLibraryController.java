package com.ideiasmidias.media.controller;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.exception.UnauthorizedException;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.media.dto.MediaLibraryResponse;
import com.ideiasmidias.media.service.MediaLibraryService;
import com.ideiasmidias.security.model.AdminUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/media-library")
@RequiredArgsConstructor
public class MediaLibraryController {

    private final MediaLibraryService mediaLibraryService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<MediaLibraryResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication required");
        }

        MediaLibraryResponse response = mediaLibraryService.upload(file, principal.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<MediaLibraryResponse>builder()
                        .success(true)
                        .message("Media uploaded successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<MediaLibraryResponse>> getById(@PathVariable Long id) {
        MediaLibraryResponse response = mediaLibraryService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<MediaLibraryResponse>builder()
                        .success(true)
                        .message("Media library item fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MediaLibraryResponse>>> getAll() {
        List<MediaLibraryResponse> response = mediaLibraryService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<MediaLibraryResponse>>builder()
                        .success(true)
                        .message("Media library items fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/type/{fileType}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MediaLibraryResponse>>> getByType(@PathVariable MediaType fileType) {
        List<MediaLibraryResponse> response = mediaLibraryService.getByType(fileType);

        return ResponseEntity.ok(
                ApiResponse.<List<MediaLibraryResponse>>builder()
                        .success(true)
                        .message("Media library items by type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/uploader/{adminUserId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MediaLibraryResponse>>> getByUploader(@PathVariable Long adminUserId) {
        List<MediaLibraryResponse> response = mediaLibraryService.getByUploader(adminUserId);

        return ResponseEntity.ok(
                ApiResponse.<List<MediaLibraryResponse>>builder()
                        .success(true)
                        .message("Media library items by uploader fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/uploader/{adminUserId}/type/{fileType}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MediaLibraryResponse>>> getByUploaderAndType(
            @PathVariable Long adminUserId,
            @PathVariable MediaType fileType
    ) {
        List<MediaLibraryResponse> response = mediaLibraryService.getByUploaderAndType(adminUserId, fileType);

        return ResponseEntity.ok(
                ApiResponse.<List<MediaLibraryResponse>>builder()
                        .success(true)
                        .message("Media library items by uploader and type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        mediaLibraryService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Media library item deleted successfully")
                        .data(null)
                        .build()
        );
    }
}