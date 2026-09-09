package com.ideiasmidias.security.service;

import com.ideiasmidias.common.exception.TooManyRequestsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginRateLimitServiceImplTest {

    private static final int MAX_ATTEMPTS = 5;
    private static final String KEY = "admin@example.com::203.0.113.10";

    private LoginRateLimitServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new LoginRateLimitServiceImpl();
    }

    @SuppressWarnings("unchecked")
    private Map<String, ?> trackedAttempts() {
        return (Map<String, ?>) ReflectionTestUtils.getField(service, "attempts");
    }

    @Test
    @DisplayName("an unknown key is allowed through")
    void unknownKeyIsAllowed() {
        assertThatCode(() -> service.checkAllowed(KEY)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("the key locks on the fifth failure, not before")
    void locksOnlyAfterMaxAttempts() {
        for (int attempt = 1; attempt < MAX_ATTEMPTS; attempt++) {
            service.recordFailure(KEY);
            assertThatCode(() -> service.checkAllowed(KEY))
                    .as("attempt %d should still be allowed", attempt)
                    .doesNotThrowAnyException();
        }

        service.recordFailure(KEY);

        assertThatThrownBy(() -> service.checkAllowed(KEY))
                .isInstanceOf(TooManyRequestsException.class);
    }

    @Test
    @DisplayName("keys are matched case-insensitively, so casing the email does not reset the count")
    void normalizesKeyCasing() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            service.recordFailure("  ADMIN@example.com::203.0.113.10  ");
        }

        assertThatThrownBy(() -> service.checkAllowed(KEY))
                .isInstanceOf(TooManyRequestsException.class);
    }

    @Test
    @DisplayName("a successful login clears the key")
    void resetClearsTheKey() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            service.recordFailure(KEY);
        }

        service.reset(KEY);

        assertThatCode(() -> service.checkAllowed(KEY)).doesNotThrowAnyException();
        assertThat(trackedAttempts()).isEmpty();
    }

    @Test
    @DisplayName("stale entries are dropped, so login spraying cannot grow the map without bound")
    void staleEntriesAreEvicted() {
        // Well past EVICTION_INTERVAL_WRITES, each with a unique attacker-chosen
        // email — the shape of a spraying script.
        for (int i = 0; i < 500; i++) {
            service.recordFailure("victim" + i + "@example.com::203.0.113.10");
        }

        int beforeExpiry = trackedAttempts().size();
        assertThat(beforeExpiry).isPositive();

        // Age every entry past the attempt window, then force a sweep.
        trackedAttempts().values().forEach(state ->
                ReflectionTestUtils.setField(
                        state,
                        "lastFailureAt",
                        java.time.LocalDateTime.now().minusHours(1)
                )
        );

        for (int i = 0; i < 100; i++) {
            service.recordFailure("fresh" + i + "@example.com::203.0.113.10");
        }

        assertThat(trackedAttempts().size())
                .as("expired entries should have been swept")
                .isLessThan(beforeExpiry + 100);
    }
}
