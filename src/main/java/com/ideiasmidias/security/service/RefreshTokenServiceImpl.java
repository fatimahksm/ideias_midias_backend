package com.ideiasmidias.security.service;

import com.ideiasmidias.adminuser.entity.AdminUser;
import com.ideiasmidias.common.exception.UnauthorizedException;
import com.ideiasmidias.security.entity.RefreshToken;
import com.ideiasmidias.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String createRefreshToken(AdminUser adminUser) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAdminUser(adminUser);
        refreshToken.setToken(generateSecureToken());
        refreshToken.setExpiresAt(LocalDateTime.now().plus(refreshExpirationMs, ChronoUnit.MILLIS));
        refreshToken.setIsRevoked(false);

        return refreshTokenRepository.save(refreshToken).getToken();
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new UnauthorizedException("Refresh token is missing.");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Refresh token is invalid."));

        if (Boolean.TRUE.equals(refreshToken.getIsRevoked())) {
            throw new UnauthorizedException("Refresh token is invalid.");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token expired. Please log in again.");
        }

        return refreshToken;
    }

    @Override
    public String rotateRefreshToken(RefreshToken currentRefreshToken) {
        LocalDateTime now = LocalDateTime.now();

        currentRefreshToken.setIsRevoked(true);
        currentRefreshToken.setRevokedAt(now);
        currentRefreshToken.setLastUsedAt(now);
        refreshTokenRepository.save(currentRefreshToken);

        return createRefreshToken(currentRefreshToken.getAdminUser());
    }

    @Override
    public Optional<RefreshToken> revokeToken(String token) {
        if (!StringUtils.hasText(token)) {
            return Optional.empty();
        }

        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(token);

        refreshTokenOptional.ifPresent(refreshToken -> {
            if (!Boolean.TRUE.equals(refreshToken.getIsRevoked())) {
                refreshToken.setIsRevoked(true);
                refreshToken.setRevokedAt(LocalDateTime.now());
                refreshTokenRepository.save(refreshToken);
            }
        });

        return refreshTokenOptional;
    }
    @Override
    public void revokeAllForAdmin(Long adminUserId) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByAdminUser_IdAndIsRevokedFalse(adminUserId);

        if (tokens.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        for (RefreshToken token : tokens) {
            token.setIsRevoked(true);
            token.setRevokedAt(now);
        }

        refreshTokenRepository.saveAll(tokens);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}