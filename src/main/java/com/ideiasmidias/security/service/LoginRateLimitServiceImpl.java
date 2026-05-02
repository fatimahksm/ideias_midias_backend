package com.ideiasmidias.security.service;

import com.ideiasmidias.common.exception.TooManyRequestsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginRateLimitServiceImpl implements LoginRateLimitService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    private final Map<String, AttemptState> attempts = new ConcurrentHashMap<>();

    @Override
    public void checkAllowed(String key) {
        AttemptState state = attempts.get(normalize(key));

        if (state == null) {
            return;
        }

        if (state.lockedUntil != null && state.lockedUntil.isAfter(LocalDateTime.now())) {
            throw new TooManyRequestsException(
                    "Too many failed login attempts. Please try again later."
            );
        }

        if (state.lockedUntil != null && state.lockedUntil.isBefore(LocalDateTime.now())) {
            attempts.remove(normalize(key));
        }
    }

    @Override
    public void recordFailure(String key) {
        String normalized = normalize(key);

        AttemptState state = attempts.getOrDefault(normalized, new AttemptState());
        state.failedAttempts++;

        if (state.failedAttempts >= MAX_ATTEMPTS) {
            state.lockedUntil = LocalDateTime.now().plusMinutes(LOCK_MINUTES);
        }

        attempts.put(normalized, state);
    }

    @Override
    public void reset(String key) {
        attempts.remove(normalize(key));
    }

    private String normalize(String key) {
        return key == null ? "unknown" : key.trim().toLowerCase();
    }

    private static class AttemptState {
        int failedAttempts = 0;
        LocalDateTime lockedUntil;
    }
}