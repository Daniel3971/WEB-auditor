package com.webauditor.backend.controller;

import com.webauditor.backend.entity.InternshipApplication;
import com.webauditor.backend.repository.InternshipApplicationRepository;
import com.webauditor.backend.CompanyService.InternshipApplicationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(
    "/api/internship-applications"
)
@CrossOrigin(
    origins = "http://localhost:4200"
)
public class InternshipApplicationController {

    private final InternshipApplicationService
        applicationService;

    private final InternshipApplicationRepository
        applicationRepository;

    public InternshipApplicationController(
        InternshipApplicationService
            applicationService,
        InternshipApplicationRepository
            applicationRepository
    ) {
        this.applicationService =
            applicationService;

        this.applicationRepository =
            applicationRepository;
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


    @GetMapping
    public List<InternshipApplication>
    getAllApplications() {

        return applicationRepository
            .findAll();
    }


    @GetMapping("/offer/{offerId}")
    public List<InternshipApplication>
    getApplicationsByOffer(
        @PathVariable Long offerId
    ) {

        return applicationRepository
            .findByInternshipOfferId(
                offerId
            );
    }
}