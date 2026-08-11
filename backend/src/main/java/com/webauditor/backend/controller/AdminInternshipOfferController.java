package com.webauditor.backend.controller;

import com.webauditor.backend.entity.InternshipOffer;
import com.webauditor.backend.entity.InternshipStatus;
import com.webauditor.backend.repository.InternshipOfferRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin/internship-offers")
public class AdminInternshipOfferController {

    private final InternshipOfferRepository
        internshipOfferRepository;


    public AdminInternshipOfferController(
        InternshipOfferRepository internshipOfferRepository
    ) {
        this.internshipOfferRepository =
            internshipOfferRepository;
    }


    /* =========================
       GET ALL
    ========================= */

    @GetMapping
    public List<InternshipOffer> getAllOffers() {

        return internshipOfferRepository
            .findAll();
    }


    /* =========================
       GET ONE
    ========================= */

    @GetMapping("/{id}")
    public ResponseEntity<?> getOffer(
        @PathVariable Long id
    ) {

        return internshipOfferRepository
            .findById(id)
            .<ResponseEntity<?>>map(
                ResponseEntity::ok
            )
            .orElseGet(
                () ->
                    ResponseEntity
                        .notFound()
                        .build()
            );
    }


    /* =========================
       CREATE
    ========================= */

    @PostMapping
    public ResponseEntity<?> createOffer(
        @RequestBody InternshipOfferRequest request
    ) {

        String validationError =
            validateRequest(request);

        if (validationError != null) {

            return ResponseEntity
                .badRequest()
                .body(
                    Map.of(
                        "message",
                        validationError
                    )
                );
        }


        InternshipOffer offer =
            new InternshipOffer();


        applyRequest(
            offer,
            request
        );


        offer.setCurrentCandidates(
            0
        );


        if (request.status() == null) {

            offer.setStatus(
                InternshipStatus.OPEN
            );

        } else {

            offer.setStatus(
                request.status()
            );
        }


        InternshipOffer saved =
            internshipOfferRepository
                .save(offer);


        return ResponseEntity.ok(
            saved
        );
    }


    /* =========================
       UPDATE
    ========================= */

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOffer(
        @PathVariable Long id,
        @RequestBody InternshipOfferRequest request
    ) {

        String validationError =
            validateRequest(request);


        if (validationError != null) {

            return ResponseEntity
                .badRequest()
                .body(
                    Map.of(
                        "message",
                        validationError
                    )
                );
        }


        InternshipOffer offer =
            internshipOfferRepository
                .findById(id)
                .orElse(null);


        if (offer == null) {

            return ResponseEntity
                .notFound()
                .build();
        }


        applyRequest(
            offer,
            request
        );


        if (request.status() != null) {

            offer.setStatus(
                request.status()
            );
        }


        InternshipOffer saved =
            internshipOfferRepository
                .save(offer);


        return ResponseEntity.ok(
            saved
        );
    }


    /* =========================
       CHANGE STATUS
    ========================= */

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
        @PathVariable Long id,
        @RequestBody Map<String, String> body
    ) {

        InternshipOffer offer =
            internshipOfferRepository
                .findById(id)
                .orElse(null);


        if (offer == null) {

            return ResponseEntity
                .notFound()
                .build();
        }


        String statusValue =
            body.get("status");


        if (statusValue == null) {

            return ResponseEntity
                .badRequest()
                .body(
                    Map.of(
                        "message",
                        "Status is required."
                    )
                );
        }


        InternshipStatus status;


        try {

            status =
                InternshipStatus.valueOf(
                    statusValue.toUpperCase()
                );

        } catch (
            IllegalArgumentException exception
        ) {

            return ResponseEntity
                .badRequest()
                .body(
                    Map.of(
                        "message",
                        "Invalid internship status."
                    )
                );
        }


        /*
         * Do not reopen a full offer.
         */
        if (
            status == InternshipStatus.OPEN &&
            offer.getCurrentCandidates() != null &&
            offer.getMaxCandidates() != null &&
            offer.getCurrentCandidates()
                >= offer.getMaxCandidates()
        ) {

            return ResponseEntity
                .badRequest()
                .body(
                    Map.of(
                        "message",
                        "This offer cannot be reopened because it has reached its maximum number of candidates."
                    )
                );
        }


        offer.setStatus(
            status
        );


        InternshipOffer saved =
            internshipOfferRepository
                .save(offer);


        return ResponseEntity.ok(
            saved
        );
    }


    /* =========================
       APPLY REQUEST DATA
    ========================= */

    private void applyRequest(
        InternshipOffer offer,
        InternshipOfferRequest request
    ) {

        offer.setTitleEn(
            request.titleEn().trim()
        );

        offer.setTitleAr(
            request.titleAr().trim()
        );

        offer.setDescriptionEn(
            request.descriptionEn().trim()
        );

        offer.setDescriptionAr(
            request.descriptionAr().trim()
        );

        offer.setMissionEn(
            request.missionEn().trim()
        );

        offer.setMissionAr(
            request.missionAr().trim()
        );

        offer.setProfileEn(
            request.profileEn().trim()
        );

        offer.setProfileAr(
            request.profileAr().trim()
        );

        offer.setDuration(
            request.duration().trim()
        );

        offer.setApplicationDeadline(
            request.applicationDeadline()
        );

        offer.setMaxCandidates(
            request.maxCandidates()
        );
    }


    /* =========================
       VALIDATION
    ========================= */

    private String validateRequest(
        InternshipOfferRequest request
    ) {

        if (
            request.titleEn() == null ||
            request.titleEn().isBlank()
        ) {
            return "English title is required.";
        }


        if (
            request.titleAr() == null ||
            request.titleAr().isBlank()
        ) {
            return "Arabic title is required.";
        }


        if (
            request.descriptionEn() == null ||
            request.descriptionEn().isBlank()
        ) {
            return "English description is required.";
        }


        if (
            request.descriptionAr() == null ||
            request.descriptionAr().isBlank()
        ) {
            return "Arabic description is required.";
        }


        if (
            request.missionEn() == null ||
            request.missionEn().isBlank()
        ) {
            return "English mission is required.";
        }


        if (
            request.missionAr() == null ||
            request.missionAr().isBlank()
        ) {
            return "Arabic mission is required.";
        }


        if (
            request.profileEn() == null ||
            request.profileEn().isBlank()
        ) {
            return "English profile is required.";
        }


        if (
            request.profileAr() == null ||
            request.profileAr().isBlank()
        ) {
            return "Arabic profile is required.";
        }


        if (
            request.duration() == null ||
            request.duration().isBlank()
        ) {
            return "Duration is required.";
        }


        if (
            request.applicationDeadline() == null
        ) {
            return "Application deadline is required.";
        }


        if (
            request.maxCandidates() == null ||
            request.maxCandidates() < 1
        ) {
            return "Maximum candidates must be at least 1.";
        }


        return null;
    }


    /* =========================
       REQUEST DTO
    ========================= */

    public record InternshipOfferRequest(

        String titleEn,

        String titleAr,

        String descriptionEn,

        String descriptionAr,

        String missionEn,

        String missionAr,

        String profileEn,

        String profileAr,

        String duration,

        LocalDate applicationDeadline,

        Integer maxCandidates,

        InternshipStatus status

    ) {}
}