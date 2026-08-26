package com.webauditor.backend.config;

import com.webauditor.backend.CompanyService.AdminUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
public class SecurityConfig {

    @Value("${app.security.secure-cookie:false}")
    private boolean secureCookie;

    @Value("${app.security.frontend-origin:http://localhost:4200}")
    private String frontendOrigin;


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
        DaoAuthenticationProvider provider,
        CookieCsrfTokenRepository csrfTokenRepository,
        SessionRegistry sessionRegistry
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


            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfTokenRepository)
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers(request ->
                    "POST".equals(request.getMethod()) &&
                    "/api/internship-applications".equals(request.getRequestURI())
                )
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
                        HttpMethod.GET,
                        "/api/internship-offers/**",
                        "/api/services/**",
                        "/api/turnstile/config",
                        "/api/hello"
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
                        HttpMethod.POST,
                        "/api/internship-applications"
                    )
                    .permitAll()


                    /* =====================
                       AUTH ENDPOINTS
                    ===================== */

                    .requestMatchers(
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/api/auth/me",
                        "/api/auth/csrf",
                        "/error"
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
                    .authenticated()
            )


            /* =========================
               SESSION MANAGEMENT
            ========================= */

            .sessionManagement(
                    session ->
                    session
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry)
            );


        return http.build();
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository =
            CookieCsrfTokenRepository.withHttpOnlyFalse();

        repository.setCookieCustomizer(cookie -> cookie
            .path("/")
            .sameSite("Lax")
            .secure(secureCookie)
        );

        return repository;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(
        SessionRegistry sessionRegistry
    ) {
        ConcurrentSessionControlAuthenticationStrategy concurrent =
            new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
        concurrent.setMaximumSessions(1);
        concurrent.setExceptionIfMaximumExceeded(false);

        return new CompositeSessionAuthenticationStrategy(
            List.of(
                concurrent,
                new SessionFixationProtectionStrategy(),
                new RegisterSessionAuthenticationStrategy(sessionRegistry)
            )
        );
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
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
                frontendOrigin
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
