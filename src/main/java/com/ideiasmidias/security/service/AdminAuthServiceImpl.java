package com.ideiasmidias.security.service;

import com.ideiasmidias.security.dto.AdminLoginRequest;
import com.ideiasmidias.security.dto.AdminLoginResponse;
import com.ideiasmidias.security.model.AdminUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        AdminUserPrincipal principal = (AdminUserPrincipal) authentication.getPrincipal();
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