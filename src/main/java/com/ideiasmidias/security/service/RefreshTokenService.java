package com.ideiasmidias.security.service;

import com.ideiasmidias.adminuser.entity.AdminUser;
import com.ideiasmidias.security.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    String createRefreshToken(AdminUser adminUser);

    RefreshToken validateRefreshToken(String token);

    String rotateRefreshToken(RefreshToken currentRefreshToken);

    Optional<RefreshToken> revokeToken(String token);

    void revokeAllForAdmin(Long adminUserId);
}