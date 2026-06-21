package com.ideiasmidias.security.service;

import com.ideiasmidias.common.exception.TooManyRequestsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginRateLimitServiceImplTest {

    private LoginRateLimitServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new LoginRateLimitServiceImpl();
    }

    @Test
    void allowsRequestsForUnknownKey() {
        assertThatCode(() -> service.checkAllowed("new@example.com"))
                .doesNotThrowAnyException();
    }

    @Test
    void allowsRequestsBelowTheThreshold() {
        String key = "user@example.com";
        for (int i = 0; i < 4; i++) {
            service.recordFailure(key);
        }

        assertThatCode(() -> service.checkAllowed(key)).doesNotThrowAnyException();
    }

    @Test
    void locksOutAfterReachingMaxAttempts() {
        String key = "user@example.com";
        for (int i = 0; i < 5; i++) {
            service.recordFailure(key);
        }

        assertThatThrownBy(() -> service.checkAllowed(key))
                .isInstanceOf(TooManyRequestsException.class);
    }

    @Test
    void resetClearsTheLockout() {
        String key = "user@example.com";
        for (int i = 0; i < 5; i++) {
            service.recordFailure(key);
        }

        service.reset(key);

        assertThatCode(() -> service.checkAllowed(key)).doesNotThrowAnyException();
    }

    @Test
    void keyMatchingIsCaseInsensitiveAndTrimmed() {
        for (int i = 0; i < 5; i++) {
            service.recordFailure("User@Example.com");
        }

        // Same identity, different casing / whitespace, must still be locked.
        assertThatThrownBy(() -> service.checkAllowed("  user@example.com  "))
                .isInstanceOf(TooManyRequestsException.class);
    }
}
