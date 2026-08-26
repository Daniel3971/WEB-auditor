package com.webauditor.backend.controller;

import com.webauditor.backend.dto.AdminLoginRequest;
import com.webauditor.backend.CompanyService.AdminAuditService;
import com.webauditor.backend.CompanyService.LoginAttemptService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfToken;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@RestController
@RequestMapping("/api/auth")
public class AdminAuthController {

    private final AuthenticationManager
        authenticationManager;

    private final SessionAuthenticationStrategy
        sessionAuthenticationStrategy;

    private final LoginAttemptService loginAttemptService;

    private final ConcurrentMap<String, HttpSession>
        activeAdminSessions = new ConcurrentHashMap<>();


    public AdminAuthController(
        AuthenticationManager authenticationManager,
        SessionAuthenticationStrategy sessionAuthenticationStrategy,
        LoginAttemptService loginAttemptService
    ) {

        this.authenticationManager =
            authenticationManager;

        this.sessionAuthenticationStrategy =
            sessionAuthenticationStrategy;

        this.loginAttemptService = loginAttemptService;

    }


    /* =========================
       LOGIN
    ========================= */

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @RequestBody AdminLoginRequest request,
        HttpServletRequest servletRequest,
        HttpServletResponse servletResponse
    ) {

        String username = request.getUsername();
        String clientIp = AdminAuditService.clientIp(servletRequest);

        if (loginAttemptService.isBlocked(username, clientIp)) {
            return invalidCredentials();
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    username,
                    request.getPassword()
                )
            );
        } catch (AuthenticationException exception) {
            loginAttemptService.loginFailed(username, clientIp);
            return invalidCredentials();
        }

        loginAttemptService.loginSucceeded(username, clientIp);

        sessionAuthenticationStrategy.onAuthentication(
            authentication,
            servletRequest,
            servletResponse
        );


        SecurityContext context =
            SecurityContextHolder
                .createEmptyContext();


        context.setAuthentication(
            authentication
        );


        SecurityContextHolder
            .setContext(
                context
            );


        HttpSession session =
            servletRequest.getSession(true);

        HttpSession previousSession = activeAdminSessions.put(
            authentication.getName(),
            session
        );

        if (previousSession != null && previousSession != session) {
            try {
                previousSession.invalidate();
            } catch (IllegalStateException ignored) {
                // The previous session had already expired.
            }
        }


        session.setAttribute(
            HttpSessionSecurityContextRepository
                .SPRING_SECURITY_CONTEXT_KEY,
            context
        );


        return ResponseEntity.ok(
            Map.of(
                "username",
                authentication.getName(),

                "roles",
                authentication
                    .getAuthorities()
            )
        );
    }

    private ResponseEntity<?> invalidCredentials() {
        return ResponseEntity.status(401).body(
            Map.of("message", "Invalid username or password.")
        );
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        return Map.of(
            "token", csrfToken.getToken(),
            "headerName", csrfToken.getHeaderName()
        );
    }


    /* =========================
       CURRENT ADMIN
    ========================= */

    @GetMapping("/me")
    public ResponseEntity<?> me(
        Authentication authentication
    ) {

        if (
            authentication == null ||
            !authentication.isAuthenticated()
        ) {

            return ResponseEntity
                .status(401)
                .body(
                    Map.of(
                        "authenticated",
                        false
                    )
                );
        }


        return ResponseEntity.ok(
            Map.of(
                "authenticated",
                true,

                "username",
                authentication.getName(),

                "roles",
                authentication
                    .getAuthorities()
            )
        );
    }


    /* =========================
       LOGOUT
    ========================= */

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
        HttpServletRequest request,
        HttpServletResponse response
    ) {

        HttpSession session =
            request.getSession(false);

        Authentication authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();


        if (session != null) {
            if (authentication != null) {
                activeAdminSessions.remove(
                    authentication.getName(),
                    session
                );
            }

            session.invalidate();
        }


        SecurityContextHolder
            .clearContext();

        response.addHeader(
            "Set-Cookie",
            "JSESSIONID=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax"
                + (request.isSecure() ? "; Secure" : "")
        );


        return ResponseEntity.ok(
            Map.of(
                "message",
                "Logged out successfully."
            )
        );
    }
}
