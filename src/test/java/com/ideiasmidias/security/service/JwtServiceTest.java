package com.ideiasmidias.security.service;

import com.ideiasmidias.adminuser.entity.AdminUser;
import com.ideiasmidias.common.enums.AdminRole;
import com.ideiasmidias.security.model.AdminUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pure unit tests for {@link JwtService}. No Spring context or database is required:
 * the configuration values normally injected via {@code @Value} are set with
 * {@link ReflectionTestUtils} so the test runs anywhere.
 */
class JwtServiceTest {

    // Base64 of a 60-byte secret, long enough for HMAC-SHA signing.
    private static final String TEST_SECRET =
            "bXktdGVzdC1qd3Qtc2VjcmV0LWtleS1mb3ItdW5pdC10ZXN0cy1uZWVkcy10by1iZS1sb25nLWVub3VnaA==";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 900_000L);
    }

    private AdminUserPrincipal principal() {
        AdminUser user = new AdminUser();
        user.setId(42L);
        user.setFullName("Test Admin");
        user.setEmail("admin@example.com");
        user.setPasswordHash("hashed");
        user.setIsActive(true);
        user.setRole(AdminRole.SUPER_ADMIN);
        return new AdminUserPrincipal(user);
    }

    @Test
    void generatesTokenAndExtractsUsername() {
        String token = jwtService.generateAccessToken(principal());

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin@example.com");
    }

    @Test
    void freshTokenIsValidForMatchingUser() {
        AdminUserPrincipal principal = principal();
        String token = jwtService.generateAccessToken(principal);

        assertThat(jwtService.isTokenValid(token, principal)).isTrue();
    }

    @Test
    void tokenIsInvalidForDifferentUser() {
        String token = jwtService.generateAccessToken(principal());

        AdminUser other = new AdminUser();
        other.setId(7L);
        other.setEmail("someone-else@example.com");
        other.setPasswordHash("x");
        other.setIsActive(true);
        other.setRole(AdminRole.ADMIN);

        assertThat(jwtService.isTokenValid(token, new AdminUserPrincipal(other))).isFalse();
    }

    @Test
    void exposesConfiguredExpiration() {
        assertThat(jwtService.getAccessTokenExpirationMs()).isEqualTo(900_000L);
    }
}
