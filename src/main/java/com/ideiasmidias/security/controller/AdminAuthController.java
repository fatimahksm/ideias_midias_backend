package com.ideiasmidias.security.controller;

import com.ideiasmidias.common.exception.UnauthorizedException;
import com.ideiasmidias.common.response.ApiResponse;
import com.ideiasmidias.common.web.RequestInfoUtil;
import com.ideiasmidias.security.dto.AdminAuthSession;
import com.ideiasmidias.security.dto.AdminLoginRequest;
import com.ideiasmidias.security.dto.AdminLoginResponse;
import com.ideiasmidias.security.dto.AdminMeResponse;
import com.ideiasmidias.security.model.AdminUserPrincipal;
import com.ideiasmidias.security.service.AdminAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @Value("${app.jwt.refresh-cookie-name:admin_refresh_token}")
    private String refreshCookieName;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    @Value("${app.jwt.refresh-cookie-secure:false}")
    private boolean refreshCookieSecure;

    @Value("${app.jwt.refresh-cookie-same-site:Lax}")
    private String refreshCookieSameSite;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AdminLoginResponse>> login(
            @Valid @RequestBody AdminLoginRequest request,
            HttpServletRequest httpRequest
    ) {
    	AdminAuthSession session = adminAuthService.login(
    	        request,
    	        RequestInfoUtil.getClientIp(httpRequest),
    	        RequestInfoUtil.getUserAgent(httpRequest)
    	);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(session.getRefreshToken()).toString())
                .body(
                        ApiResponse.<AdminLoginResponse>builder()
                                .success(true)
                                .message("Admin login successful")
                                .data(session.getResponse())
                                .build()
                );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AdminLoginResponse>> refresh(
            HttpServletRequest request
    ) {
        String refreshToken = extractRefreshTokenFromCookies(request);
        AdminAuthSession session = adminAuthService.refresh(
                refreshToken,
                RequestInfoUtil.getClientIp(request),
                RequestInfoUtil.getUserAgent(request)
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(session.getRefreshToken()).toString())
                .body(
                        ApiResponse.<AdminLoginResponse>builder()
                                .success(true)
                                .message("Access token refreshed successfully")
                                .data(session.getResponse())
                                .build()
                );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request
    ) {
        String refreshToken = extractRefreshTokenFromCookies(request);
        adminAuthService.logout(
                refreshToken,
                RequestInfoUtil.getClientIp(request),
                RequestInfoUtil.getUserAgent(request)
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildClearRefreshCookie().toString())
                .body(
                        ApiResponse.<Void>builder()
                                .success(true)
                                .message("Admin logout successful")
                                .data(null)
                                .build()
                );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AdminMeResponse>> me(
            @AuthenticationPrincipal AdminUserPrincipal principal
    ) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication required");
        }

        AdminMeResponse response = AdminMeResponse.builder()
                .adminId(principal.getId())
                .fullName(principal.getFullName())
                .email(principal.getEmail())
                .role(principal.getRole())
                .isActive(principal.getIsActive())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<AdminMeResponse>builder()
                        .success(true)
                        .message("Current admin fetched successfully")
                        .data(response)
                        .build()
        );
    }

    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (refreshCookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from(refreshCookieName, refreshToken)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .path("/api/admin/auth")
                .sameSite(refreshCookieSameSite)
                .maxAge(Duration.ofMillis(refreshExpirationMs))
                .build();
    }

    private ResponseCookie buildClearRefreshCookie() {
        return ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .path("/api/admin/auth")
                .sameSite(refreshCookieSameSite)
                .maxAge(Duration.ZERO)
                .build();
    }
}