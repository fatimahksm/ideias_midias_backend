package com.ideiasmidias.security.service;

import com.ideiasmidias.adminuser.entity.AdminUser;
import com.ideiasmidias.adminuser.repository.AdminUserRepository;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.common.exception.UnauthorizedException;
import com.ideiasmidias.security.dto.AdminLoginRequest;
import com.ideiasmidias.security.dto.AdminLoginResponse;
import com.ideiasmidias.security.model.AdminUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AdminUserRepository adminUserRepository;

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        final Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (DisabledException ex) {
            throw new UnauthorizedException(
                    "Your admin account is inactive. Please contact the super admin."
            );
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Invalid email or password.");
        } catch (AuthenticationException ex) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        AdminUserPrincipal principal = (AdminUserPrincipal) authentication.getPrincipal();

        AdminUser adminUser = adminUserRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Admin user not found with id: " + principal.getId()
                ));

        if (!Boolean.TRUE.equals(adminUser.getIsActive())) {
            throw new UnauthorizedException(
                    "Your admin account is inactive. Please contact the super admin."
            );
        }

        adminUser.setLastLoginAt(LocalDateTime.now());
        adminUserRepository.save(adminUser);

        String token = jwtService.generateToken(principal);

        return AdminLoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .adminId(principal.getId())
                .email(principal.getEmail())
                .role(principal.getRole())
                .build();
    }
}