package com.webauditor.backend.CompanyService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class LoginAttemptService {

    private final ConcurrentMap<String, AttemptState> attempts = new ConcurrentHashMap<>();
    private final int maximumAttempts;
    private final Duration lockDuration;

    public LoginAttemptService(
        @Value("${app.security.login.max-attempts:5}") int maximumAttempts,
        @Value("${app.security.login.lock-minutes:15}") long lockMinutes
    ) {
        this.maximumAttempts = maximumAttempts;
        this.lockDuration = Duration.ofMinutes(lockMinutes);
    }

    public boolean isBlocked(String username, String ipAddress) {
        String key = key(username, ipAddress);
        AttemptState state = attempts.get(key);
        if (state == null || state.blockedUntil() == null) {
            return false;
        }
        if (!Instant.now().isBefore(state.blockedUntil())) {
            attempts.remove(key, state);
            return false;
        }
        return true;
    }

    public void loginFailed(String username, String ipAddress) {
        attempts.compute(key(username, ipAddress), (key, current) -> {
            int failures = current == null ? 1 : current.failures() + 1;
            Instant blockedUntil = failures >= maximumAttempts
                ? Instant.now().plus(lockDuration)
                : null;
            return new AttemptState(failures, blockedUntil);
        });
    }

    public void loginSucceeded(String username, String ipAddress) {
        attempts.remove(key(username, ipAddress));
    }

    private String key(String username, String ipAddress) {
        String normalizedUsername = username == null
            ? ""
            : username.trim().toLowerCase(Locale.ROOT);
        return normalizedUsername + "|" + ipAddress;
    }

    private record AttemptState(int failures, Instant blockedUntil) {}
}
