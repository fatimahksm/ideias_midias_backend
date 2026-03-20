package com.ideiasmidias.security.controller;

import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.security.dto.AdminLoginRequest;
import com.ideiasmidias.security.dto.AdminLoginResponse;
import com.ideiasmidias.security.service.AdminAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AdminLoginResponse>> login(@Valid @RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminAuthService.login(request);

        return ResponseEntity.ok(
                ApiResponse.<AdminLoginResponse>builder()
                        .success(true)
                        .message("Admin login successful")
                        .data(response)
                        .build()
        );
    }
}