package com.webauditor.backend.CompanyService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class PublicApplicationRateLimitService {

    private final ConcurrentMap<String, Deque<Instant>> attemptsByIp =
        new ConcurrentHashMap<>();

    private final int maximumAttempts;
    private final Duration window;

    public PublicApplicationRateLimitService(
        @Value("${app.security.application.max-attempts:10}") int maximumAttempts,
        @Value("${app.security.application.window-minutes:60}") long windowMinutes
    ) {
        this.maximumAttempts = maximumAttempts;
        this.window = Duration.ofMinutes(windowMinutes);
    }

    /**
     * Counts each submission attempt so automated invalid uploads cannot bypass
     * the limit. A normal visitor can submit up to ten applications per hour.
     */
    public boolean tryAcquire(HttpServletRequest request) {
        String ipAddress = AdminAuditService.clientIp(request);
        Instant now = Instant.now();
        Instant oldestAllowed = now.minus(window);
        Deque<Instant> attempts = attemptsByIp.computeIfAbsent(
            ipAddress,
            ignored -> new ArrayDeque<>()
        );

        synchronized (attempts) {
            while (!attempts.isEmpty() && attempts.peekFirst().isBefore(oldestAllowed)) {
                attempts.removeFirst();
            }

            if (attempts.size() >= maximumAttempts) {
                return false;
            }

            attempts.addLast(now);
            return true;
        }
    }
}
