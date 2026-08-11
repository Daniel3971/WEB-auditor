package com.webauditor.backend.controller;

import com.webauditor.backend.dto.AdminLoginRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AdminAuthController {

    private final AuthenticationManager
        authenticationManager;


    public AdminAuthController(
        AuthenticationManager authenticationManager
    ) {

        this.authenticationManager =
            authenticationManager;
    }


    /* =========================
       LOGIN
    ========================= */

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @RequestBody AdminLoginRequest request,
        HttpServletRequest servletRequest
    ) {

        Authentication authentication =
            authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
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
        HttpServletRequest request
    ) {

        HttpSession session =
            request.getSession(false);


        if (session != null) {
            session.invalidate();
        }


        SecurityContextHolder
            .clearContext();


        return ResponseEntity.ok(
            Map.of(
                "message",
                "Logged out successfully."
            )
        );
    }
}