package com.storetemplate.store.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory brute-force guard for the login form: after {@value #MAX_ATTEMPTS}
 * failed attempts from one client IP within the window, that IP is blocked for
 * {@link #WINDOW_MS}. State is per-instance (fine for a single free dyno); a
 * multi-instance deployment would move this to Redis.
 */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 15 * 60 * 1000L;   // 15 minutes

    private static final class Attempts {
        int count;
        long windowStart;
        long blockedUntil;
    }

    private final Map<String, Attempts> cache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        if (key != null) cache.remove(key);
    }

    public void loginFailed(String key) {
        if (key == null) return;
        long now = System.currentTimeMillis();
        cache.compute(key, (k, a) -> {
            if (a == null || now - a.windowStart > WINDOW_MS) {
                a = new Attempts();
                a.windowStart = now;
            }
            a.count++;
            if (a.count >= MAX_ATTEMPTS) {
                a.blockedUntil = now + WINDOW_MS;
            }
            return a;
        });
    }

    public boolean isBlocked(String key) {
        if (key == null) return false;
        Attempts a = cache.get(key);
        return a != null && a.blockedUntil > System.currentTimeMillis();
    }
}
