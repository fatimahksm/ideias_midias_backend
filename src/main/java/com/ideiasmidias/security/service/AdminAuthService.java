package com.ideiasmidias.security.service;

import com.ideiasmidias.security.dto.AdminAuthSession;
import com.ideiasmidias.security.dto.AdminLoginRequest;

public interface AdminAuthService {

    AdminAuthSession login(
            AdminLoginRequest request,
            String ipAddress,
            String userAgent
    );

    AdminAuthSession refresh(
            String refreshToken,
            String ipAddress,
            String userAgent
    );

    void logout(
            String refreshToken,
            String ipAddress,
            String userAgent
    );
}