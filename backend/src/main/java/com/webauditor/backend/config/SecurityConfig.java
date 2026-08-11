package com.webauditor.backend.config;

import com.webauditor.backend.CompanyService.AdminUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
public class SecurityConfig {


    /* =========================
       PASSWORD ENCODER
    ========================= */

    @Bean
    public PasswordEncoder passwordEncoder() {

        return PasswordEncoderFactories
            .createDelegatingPasswordEncoder();
    }


    /* =========================
       AUTHENTICATION PROVIDER
    ========================= */

    @Bean
    public DaoAuthenticationProvider
    authenticationProvider(
        AdminUserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder
    ) {

        DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider(
                userDetailsService
            );

        provider.setPasswordEncoder(
            passwordEncoder
        );

        return provider;
    }


    /* =========================
       AUTHENTICATION MANAGER
    ========================= */

    @Bean
    public AuthenticationManager
    authenticationManager(
        AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration
            .getAuthenticationManager();
    }


    /* =========================
       SECURITY FILTER
    ========================= */

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        DaoAuthenticationProvider provider
    ) throws Exception {

        http

            /* =========================
               AUTHENTICATION PROVIDER
            ========================= */

            .authenticationProvider(
                provider
            )


            /* =========================
               CORS
            ========================= */

            .cors(cors -> {})


            /* =========================
               CSRF

               TEMPORARILY disabled.

               We will enable it after
               Angular XSRF support is
               configured correctly.
            ========================= */

            .csrf(
                csrf ->
                    csrf.disable()
            )


            /* =========================
               AUTHORIZATION
            ========================= */

            .authorizeHttpRequests(
                auth -> auth


                    /* =====================
                       PUBLIC INTERNSHIP API
                    ===================== */

                    .requestMatchers(
                        "/api/internship-offers/**"
                    )
                    .permitAll()


                    /* =====================
                       PUBLIC APPLICATION
                       SUBMISSION

                       Important:
                       only the base POST
                       endpoint should be
                       public.
                    ===================== */

                    .requestMatchers(
                        "/api/internship-applications"
                    )
                    .permitAll()


                    /* =====================
                       AUTH ENDPOINTS
                    ===================== */

                    .requestMatchers(
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/api/auth/me"
                    )
                    .permitAll()


                    /* =====================
                       ADMIN API

                       Only approved roles
                       can access these.
                    ===================== */

                    .requestMatchers(
                        "/api/admin/**"
                    )
                    .hasAnyRole(
                        "ADMIN",
                        "SUPER_ADMIN",
                        "DEVELOPER"
                    )


                    /* =====================
                       EVERYTHING ELSE
                    ===================== */

                    .anyRequest()
                    .permitAll()
            )


            /* =========================
               SESSION MANAGEMENT
            ========================= */

            .sessionManagement(
                session ->
                    session
                        .maximumSessions(1)
            );


        return http.build();
    }


    /* =========================
       CORS CONFIGURATION
    ========================= */

    @Bean
    public CorsConfigurationSource
    corsConfigurationSource() {

        CorsConfiguration configuration =
            new CorsConfiguration();


        /* =========================
           FRONTEND ORIGIN
        ========================= */

        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:4200"
            )
        );


        /* =========================
           ALLOWED HTTP METHODS
        ========================= */

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
        );


        /* =========================
           ALLOWED HEADERS
        ========================= */

        configuration.setAllowedHeaders(
            List.of("*")
        );


        /* =========================
           SESSION COOKIE

           Required because Angular
           sends JSESSIONID.
        ========================= */

        configuration.setAllowCredentials(
            true
        );


        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
            "/**",
            configuration
        );


        return source;
    }
}