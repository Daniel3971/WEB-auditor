package com.webauditor.backend.controller;

import com.webauditor.backend.entity.InternshipApplication;
import com.webauditor.backend.CompanyService.InternshipApplicationService;

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

    public InternshipApplicationController(
        InternshipApplicationService
            applicationService
    ) {
        this.applicationService =
            applicationService;
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
        MultipartFile cv
    ) {

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
