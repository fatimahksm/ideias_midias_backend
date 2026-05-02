package com.ideiasmidias.security.service;

public interface LoginRateLimitService {

    void checkAllowed(String key);

    void recordFailure(String key);

    void reset(String key);
}