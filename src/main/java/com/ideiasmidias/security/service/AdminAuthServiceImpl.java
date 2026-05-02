package com.ideiasmidias.security.service;

import com.ideiasmidias.adminuser.entity.AdminUser;
import com.ideiasmidias.adminuser.repository.AdminUserRepository;
import com.ideiasmidias.audit.service.AdminAuditLogService;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.common.exception.TooManyRequestsException;
import com.ideiasmidias.common.exception.UnauthorizedException;
import com.ideiasmidias.security.dto.AdminAuthSession;
import com.ideiasmidias.security.dto.AdminLoginRequest;
import com.ideiasmidias.security.dto.AdminLoginResponse;
import com.ideiasmidias.security.entity.RefreshToken;
import com.ideiasmidias.security.model.AdminUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AdminUserRepository adminUserRepository;
    private final RefreshTokenService refreshTokenService;
    private final LoginRateLimitService loginRateLimitService;
    private final AdminAuditLogService auditLogService;
    
    @Override
    public AdminAuthSession login(
            AdminLoginRequest request,
            String ipAddress,
            String userAgent
    ) {
        final Authentication authentication;
        String emailKey = request.getEmail();
        String rateLimitKey = buildLoginRateLimitKey(emailKey, ipAddress);

        try {
            loginRateLimitService.checkAllowed(rateLimitKey);
        } catch (TooManyRequestsException ex) {
            auditLogService.log(
                    null,
                    emailKey,
                    "ADMIN_LOGIN_RATE_LIMITED",
                    false,
                    ipAddress,
                    userAgent,
                    ex.getMessage()
            );
            throw ex;
        }

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (DisabledException ex) {
            auditLogService.log(
                    null,
                    emailKey,
                    "ADMIN_LOGIN_DISABLED_ACCOUNT",
                    false,
                    ipAddress,
                    userAgent,
                    "Admin account is inactive."
            );

            throw new UnauthorizedException(
                    "Your admin account is inactive. Please contact the super admin."
            );
        } catch (BadCredentialsException ex) {
        	loginRateLimitService.recordFailure(rateLimitKey);

            auditLogService.log(
                    null,
                    emailKey,
                    "ADMIN_LOGIN_FAILED",
                    false,
                    ipAddress,
                    userAgent,
                    "Invalid email or password."
            );

            throw new UnauthorizedException("Invalid email or password.");
        } catch (AuthenticationException ex) {
        	loginRateLimitService.recordFailure(rateLimitKey);

            auditLogService.log(
                    null,
                    emailKey,
                    "ADMIN_LOGIN_FAILED",
                    false,
                    ipAddress,
                    userAgent,
                    "Authentication failed."
            );

            throw new UnauthorizedException("Invalid email or password.");
        }

        AdminUserPrincipal principal = (AdminUserPrincipal) authentication.getPrincipal();

        AdminUser adminUser = adminUserRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Admin user not found with id: " + principal.getId()
                ));

        if (!Boolean.TRUE.equals(adminUser.getIsActive())) {
            auditLogService.log(
                    adminUser.getId(),
                    adminUser.getEmail(),
                    "ADMIN_LOGIN_DISABLED_ACCOUNT",
                    false,
                    ipAddress,
                    userAgent,
                    "Admin account is inactive."
            );

            throw new UnauthorizedException(
                    "Your admin account is inactive. Please contact the super admin."
            );
        }

        adminUser.setLastLoginAt(LocalDateTime.now());
        adminUserRepository.save(adminUser);

        refreshTokenService.revokeAllForAdmin(adminUser.getId());

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = refreshTokenService.createRefreshToken(adminUser);

        loginRateLimitService.reset(rateLimitKey);

        auditLogService.log(
                adminUser.getId(),
                adminUser.getEmail(),
                "ADMIN_LOGIN_SUCCESS",
                true,
                ipAddress,
                userAgent,
                "Admin login successful."
        );

        return AdminAuthSession.builder()
                .refreshToken(refreshToken)
                .response(
                        AdminLoginResponse.builder()
                                .token(accessToken)
                                .tokenType("Bearer")
                                .expiresInMs(jwtService.getAccessTokenExpirationMs())
                                .adminId(principal.getId())
                                .email(principal.getEmail())
                                .role(principal.getRole())
                                .build()
                )
                .build();
    }

    @Override
    public AdminAuthSession refresh(
            String refreshTokenValue,
            String ipAddress,
            String userAgent
    ) {
        try {
            RefreshToken currentRefreshToken =
                    refreshTokenService.validateRefreshToken(refreshTokenValue);

            AdminUser adminUser = currentRefreshToken.getAdminUser();

            if (!Boolean.TRUE.equals(adminUser.getIsActive())) {
                auditLogService.log(
                        adminUser.getId(),
                        adminUser.getEmail(),
                        "ADMIN_REFRESH_DISABLED_ACCOUNT",
                        false,
                        ipAddress,
                        userAgent,
                        "Admin account is inactive."
                );

                throw new UnauthorizedException(
                        "Your admin account is inactive. Please contact the super admin."
                );
            }

            AdminUserPrincipal principal = new AdminUserPrincipal(adminUser);

            String newAccessToken = jwtService.generateAccessToken(principal);
            String newRefreshToken =
                    refreshTokenService.rotateRefreshToken(currentRefreshToken);

            auditLogService.log(
                    adminUser.getId(),
                    adminUser.getEmail(),
                    "ADMIN_REFRESH_SUCCESS",
                    true,
                    ipAddress,
                    userAgent,
                    "Access token refreshed successfully."
            );

            return AdminAuthSession.builder()
                    .refreshToken(newRefreshToken)
                    .response(
                            AdminLoginResponse.builder()
                                    .token(newAccessToken)
                                    .tokenType("Bearer")
                                    .expiresInMs(jwtService.getAccessTokenExpirationMs())
                                    .adminId(principal.getId())
                                    .email(principal.getEmail())
                                    .role(principal.getRole())
                                    .build()
                    )
                    .build();

        } catch (UnauthorizedException ex) {
            auditLogService.log(
                    null,
                    null,
                    "ADMIN_REFRESH_FAILED",
                    false,
                    ipAddress,
                    userAgent,
                    ex.getMessage()
            );
            throw ex;
        }
    }

    @Override
    public void logout(
            String refreshTokenValue,
            String ipAddress,
            String userAgent
    ) {
        refreshTokenService.revokeToken(refreshTokenValue).ifPresentOrElse(
                refreshToken -> {
                    AdminUser adminUser = refreshToken.getAdminUser();

                    auditLogService.log(
                            adminUser != null ? adminUser.getId() : null,
                            adminUser != null ? adminUser.getEmail() : null,
                            "ADMIN_LOGOUT",
                            true,
                            ipAddress,
                            userAgent,
                            "Admin logout successful."
                    );
                },
                () -> auditLogService.log(
                        null,
                        null,
                        "ADMIN_LOGOUT",
                        true,
                        ipAddress,
                        userAgent,
                        "Admin logout requested without valid refresh token."
                )
        );
    }
    private String buildLoginRateLimitKey(String email, String ipAddress) {
        String cleanEmail = email == null ? "unknown-email" : email.trim().toLowerCase();
        String cleanIp = ipAddress == null || ipAddress.isBlank()
                ? "unknown-ip"
                : ipAddress.trim();

        return cleanEmail + "::" + cleanIp;
    }
}