package com.ideiasmidias.security.service;

import com.ideiasmidias.common.exception.TooManyRequestsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory throttle for admin login attempts.
 *
 * <p>State is per-instance and lost on restart, which is fine for a single-node
 * deployment: the worst case is an attacker regaining a few attempts. Behind
 * more than one instance this becomes per-instance rather than global, so a
 * shared store (Redis) would be the next step.
 *
 * <p>Keys are derived from the submitted email, so anyone can mint an unbounded
 * number of them. Entries therefore expire on their own and the map is capped —
 * without that, a login-spraying script would grow it until the JVM runs out of
 * heap.
 */
@Service
public class LoginRateLimitServiceImpl implements LoginRateLimitService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    /** Failures stop counting after this long without a new one. */
    private static final int ATTEMPT_WINDOW_MINUTES = 15;

    /** Hard ceiling on tracked keys; the oldest are dropped once it is hit. */
    private static final int MAX_TRACKED_KEYS = 10_000;

    /** How many writes go by between sweeps of expired entries. */
    private static final int EVICTION_INTERVAL_WRITES = 100;

    private final Map<String, AttemptState> attempts = new ConcurrentHashMap<>();
    private int writesSinceEviction = 0;

    @Override
    public void checkAllowed(String key) {
        String normalized = normalize(key);
        AttemptState state = attempts.get(normalized);

        if (state == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        if (state.isLockedAt(now)) {
            throw new TooManyRequestsException(
                    "Too many failed login attempts. Please try again later."
            );
        }

        // Lock served its time, or the failures are simply stale: start over.
        if (state.isExpiredAt(now)) {
            attempts.remove(normalized);
        }
    }

    @Override
    public void recordFailure(String key) {
        String normalized = normalize(key);
        LocalDateTime now = LocalDateTime.now();

        attempts.compute(normalized, (ignored, existing) -> {
            AttemptState state = existing == null || existing.isExpiredAt(now)
                    ? new AttemptState()
                    : existing;

            state.failedAttempts++;
            state.lastFailureAt = now;

            if (state.failedAttempts >= MAX_ATTEMPTS) {
                state.lockedUntil = now.plusMinutes(LOCK_MINUTES);
            }

            return state;
        });

        evictIfDue(now);
    }

    @Override
    public void reset(String key) {
        attempts.remove(normalize(key));
    }

    /**
     * Drops entries that no longer hold anyone back, and — if the map is still
     * over its ceiling — the least recently active ones on top of that, so the
     * cap holds even under a sustained spray of unique emails.
     */
    private void evictIfDue(LocalDateTime now) {
        synchronized (this) {
            if (++writesSinceEviction < EVICTION_INTERVAL_WRITES
                    && attempts.size() < MAX_TRACKED_KEYS) {
                return;
            }

            writesSinceEviction = 0;
        }

        attempts.entrySet().removeIf(entry -> entry.getValue().isExpiredAt(now));

        int overflow = attempts.size() - MAX_TRACKED_KEYS;

        if (overflow > 0) {
            List<String> oldest = attempts.entrySet().stream()
                    .sorted(Comparator.comparing(entry -> entry.getValue().lastFailureAt))
                    .limit(overflow)
                    .map(Map.Entry::getKey)
                    .toList();

            oldest.forEach(attempts::remove);
        }
    }

    private String normalize(String key) {
        return key == null ? "unknown" : key.trim().toLowerCase();
    }

    private static class AttemptState {
        int failedAttempts = 0;
        LocalDateTime lastFailureAt = LocalDateTime.now();
        LocalDateTime lockedUntil;

        boolean isLockedAt(LocalDateTime now) {
            return lockedUntil != null && lockedUntil.isAfter(now);
        }

        /** Nothing left to enforce: the lock elapsed, or the failures went stale. */
        boolean isExpiredAt(LocalDateTime now) {
            if (lockedUntil != null) {
                return !lockedUntil.isAfter(now);
            }

            return lastFailureAt.plusMinutes(ATTEMPT_WINDOW_MINUTES).isBefore(now);
        }
    }
}
