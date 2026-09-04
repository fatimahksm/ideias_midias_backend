package com.ideiasmidias.media.controller;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.exception.UnauthorizedException;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.common.response.PageResponse;
import com.ideiasmidias.media.dto.MediaLibraryResponse;
import com.ideiasmidias.media.service.MediaLibraryService;
import com.ideiasmidias.media.service.MediaStorageService;
import com.ideiasmidias.security.model.AdminUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
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
    private final MediaStorageService mediaStorageService;

    @PostMapping(
            value = "/upload",
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<MediaLibraryResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        AdminUserPrincipal currentAdmin = requirePrincipal(principal);

        MediaLibraryResponse response = mediaLibraryService.upload(file, currentAdmin.getId());

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
    public ResponseEntity<ApiResponse<MediaLibraryResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        AdminUserPrincipal currentAdmin = requirePrincipal(principal);

        MediaLibraryResponse response = mediaLibraryService.getById(id);
        ensureCanAccessMedia(currentAdmin, response);

        return ResponseEntity.ok(
                ApiResponse.<MediaLibraryResponse>builder()
                        .success(true)
                        .message("Media library item fetched successfully")
                        .data(response)
                        .build()
        );
    }

    /**
     * Streams the file's own bytes back through our origin, authenticated
     * the same way as every other admin call. Used by the crop tool so it
     * can re-read an already-uploaded image without the browser needing
     * direct (and CORS-enabled) access to wherever it's actually stored.
     */
    @GetMapping("/{id}/raw")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<InputStreamResource> getRaw(
            @PathVariable Long id,
            @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        AdminUserPrincipal currentAdmin = requirePrincipal(principal);

        MediaLibraryResponse media = mediaLibraryService.getById(id);
        ensureCanAccessMedia(currentAdmin, media);

        InputStreamResource body = new InputStreamResource(
                mediaStorageService.openStream(media.getFileUrl())
        );

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(media.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + media.getFileName() + "\"")
                .body(body);
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

    /**
     * Paged listing for the admin screens. Non-super admins only ever see
     * their own uploads, whatever they ask for.
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<MediaLibraryResponse>>> getPage(
            @RequestParam(required = false) MediaType fileType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        AdminUserPrincipal currentAdmin = requirePrincipal(principal);
        Long uploaderId = isSuperAdmin(currentAdmin) ? null : currentAdmin.getId();

        PageResponse<MediaLibraryResponse> response =
                mediaLibraryService.getPage(fileType, uploaderId, page, size);

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<MediaLibraryResponse>>builder()
                        .success(true)
                        .message("Media library page fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/type/{fileType}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MediaLibraryResponse>>> getByType(
            @PathVariable MediaType fileType
    ) {
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
    public ResponseEntity<ApiResponse<List<MediaLibraryResponse>>> getByUploader(
            @PathVariable Long adminUserId,
            @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        AdminUserPrincipal currentAdmin = requirePrincipal(principal);
        Long targetUploaderId = resolveAllowedUploaderId(currentAdmin, adminUserId);

        List<MediaLibraryResponse> response = mediaLibraryService.getByUploader(targetUploaderId);

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
            @PathVariable MediaType fileType,
            @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        AdminUserPrincipal currentAdmin = requirePrincipal(principal);
        Long targetUploaderId = resolveAllowedUploaderId(currentAdmin, adminUserId);

        List<MediaLibraryResponse> response =
                mediaLibraryService.getByUploaderAndType(targetUploaderId, fileType);

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

    private AdminUserPrincipal requirePrincipal(AdminUserPrincipal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication required");
        }

        return principal;
    }

    private Long resolveAllowedUploaderId(AdminUserPrincipal principal, Long requestedUploaderId) {
        if (isSuperAdmin(principal)) {
            return requestedUploaderId;
        }

        if (!principal.getId().equals(requestedUploaderId)) {
            throw new UnauthorizedException("You are not allowed to access media uploaded by another admin");
        }

        return requestedUploaderId;
    }

    private void ensureCanAccessMedia(AdminUserPrincipal principal, MediaLibraryResponse media) {
        if (isSuperAdmin(principal)) {
            return;
        }

        if (media.getUploadedById() == null) {
            throw new UnauthorizedException("You are not allowed to access this media item");
        }

        if (!principal.getId().equals(media.getUploadedById())) {
            throw new UnauthorizedException("You are not allowed to access this media item");
        }
    }

    private boolean isSuperAdmin(AdminUserPrincipal principal) {
        return principal != null && "SUPER_ADMIN".equalsIgnoreCase(principal.getRole());
    }
}