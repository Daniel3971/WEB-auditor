package com.webauditor.backend.controller;

import com.webauditor.backend.entity.InternshipApplication;
import com.webauditor.backend.CompanyService.InternshipApplicationService;
import com.webauditor.backend.CompanyService.PublicApplicationRateLimitService;
import com.webauditor.backend.CompanyService.TurnstileService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(
    "/api/internship-applications"
)
public class InternshipApplicationController {

    private final InternshipApplicationService
        applicationService;

    private final PublicApplicationRateLimitService rateLimitService;
    private final TurnstileService turnstileService;

    public InternshipApplicationController(
        InternshipApplicationService
            applicationService,
        PublicApplicationRateLimitService rateLimitService,
        TurnstileService turnstileService
    ) {
        this.applicationService =
            applicationService;

        this.rateLimitService = rateLimitService;
        this.turnstileService = turnstileService;
    }

    @PostMapping
    public ResponseEntity<?> apply(
        @RequestParam Long offerId,
        @RequestParam String firstName,
        @RequestParam String lastName,
        @RequestParam String email,
        @RequestParam String phone,

        @RequestParam(
            required = false
        )
        String university,

        @RequestParam(
            required = false
        )
        String major,

        @RequestParam(
            required = false
        )
        String message,

        @RequestParam("cv")
        MultipartFile cv,

        @RequestParam(name = "turnstileToken", required = false)
        String turnstileToken,
        HttpServletRequest servletRequest
    ) {

        if (!rateLimitService.tryAcquire(servletRequest)) {
            return ResponseEntity.status(429).body(
                "Too many application submissions. Please try again later."
            );
        }

        if (!turnstileService.validate(turnstileToken, servletRequest)) {
            return ResponseEntity.badRequest().body(
                "Security verification failed. Please refresh the page and try again."
            );
        }

        try {

            InternshipApplication
                application =
                    applicationService.apply(
                        offerId,
                        firstName,
                        lastName,
                        email,
                        phone,
                        university,
                        major,
                        message,
                        cv
                    );

            return ResponseEntity.ok(
                application.getId()
            );

        } catch (
            IllegalArgumentException |
            IllegalStateException e
        ) {

            return ResponseEntity
                .badRequest()
                .body(
                    e.getMessage()
                );

        } catch (IOException e) {

            return ResponseEntity
                .internalServerError()
                .body(
                    "Could not save CV."
                );
        }
    }
}
