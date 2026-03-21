package com.ideiasmidias.adminuser.controller;

import com.ideiasmidias.adminuser.dto.AdminUserResponse;
import com.ideiasmidias.adminuser.dto.CreateAdminUserRequest;
import com.ideiasmidias.adminuser.dto.ResetAdminPasswordRequest;
import com.ideiasmidias.adminuser.dto.UpdateAdminUserRequest;
import com.ideiasmidias.adminuser.dto.UpdateAdminUserStatusRequest;
import com.ideiasmidias.adminuser.service.AdminUserService;
import com.ideiasmidias.common.exception.UnauthorizedException;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.security.model.AdminUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AdminUserResponse>> createAdmin(
            @Valid @RequestBody CreateAdminUserRequest request
    ) {
        AdminUserResponse response = adminUserService.createAdmin(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<AdminUserResponse>builder()
                        .success(true)
                        .message("Admin user created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllAdmins() {
        List<AdminUserResponse> response = adminUserService.getAllAdmins();

        return ResponseEntity.ok(
                ApiResponse.<List<AdminUserResponse>>builder()
                        .success(true)
                        .message("Admin users fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getAdminById(@PathVariable Long id) {
        AdminUserResponse response = adminUserService.getAdminById(id);

        return ResponseEntity.ok(
                ApiResponse.<AdminUserResponse>builder()
                        .success(true)
                        .message("Admin user fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAdminUserRequest request,
            @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication required");
        }

        AdminUserResponse response = adminUserService.updateAdmin(id, request, principal.getId());

        return ResponseEntity.ok(
                ApiResponse.<AdminUserResponse>builder()
                        .success(true)
                        .message("Admin user updated successfully")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateAdminStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAdminUserStatusRequest request,
            @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication required");
        }

        AdminUserResponse response = adminUserService.updateAdminStatus(id, request, principal.getId());

        return ResponseEntity.ok(
                ApiResponse.<AdminUserResponse>builder()
                        .success(true)
                        .message("Admin user status updated successfully")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resetAdminPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetAdminPasswordRequest request,
            @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication required");
        }

        adminUserService.resetAdminPassword(id, request, principal.getId());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Admin user password reset successfully")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAdmin(
            @PathVariable Long id,
            @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication required");
        }

        adminUserService.deleteAdmin(id, principal.getId());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Admin user deleted successfully")
                        .data(null)
                        .build()
        );
    }
}