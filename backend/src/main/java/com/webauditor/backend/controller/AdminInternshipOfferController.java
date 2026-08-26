package com.webauditor.backend.controller;

import com.webauditor.backend.entity.InternshipOffer;
import com.webauditor.backend.entity.InternshipStatus;
import com.webauditor.backend.repository.InternshipOfferRepository;
import com.webauditor.backend.repository.InternshipApplicationRepository;
import com.webauditor.backend.CompanyService.AdminAuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

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

    private final InternshipApplicationRepository
        internshipApplicationRepository;

    private final AdminAuditService auditService;


    public AdminInternshipOfferController(
        InternshipOfferRepository internshipOfferRepository,
        InternshipApplicationRepository internshipApplicationRepository,
        AdminAuditService auditService
    ) {
        this.internshipOfferRepository =
            internshipOfferRepository;

        this.internshipApplicationRepository =
            internshipApplicationRepository;

        this.auditService = auditService;
    }


    /* =========================
       GET ALL
    ========================= */

    @GetMapping
    public List<InternshipOffer> getAllOffers() {

        return internshipOfferRepository
            .findByArchivedFalse();
    }

    @GetMapping("/archived")
    public List<InternshipOffer> getArchivedOffers() {
        return internshipOfferRepository.findByArchivedTrue();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> archiveOffer(
        @PathVariable Long id,
        Authentication authentication,
        HttpServletRequest servletRequest
    ) {
        InternshipOffer offer = internshipOfferRepository
            .findByIdAndArchivedFalse(id)
            .orElse(null);

        if (offer == null) {
            return ResponseEntity.notFound().build();
        }

        offer.setStatus(InternshipStatus.CLOSED);
        offer.setArchived(true);
        internshipOfferRepository.save(offer);
        auditService.record(authentication, servletRequest, "OFFER_ARCHIVED", "INTERNSHIP_OFFER", id, offer.getTitleEn());

        return ResponseEntity.ok(
            Map.of("message", "Internship offer removed successfully.")
        );
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<?> restoreOffer(
        @PathVariable Long id,
        Authentication authentication,
        HttpServletRequest servletRequest
    ) {
        InternshipOffer offer = internshipOfferRepository
            .findById(id)
            .orElse(null);

        if (offer == null || !offer.isArchived()) {
            return ResponseEntity.notFound().build();
        }

        offer.setArchived(false);
        offer.setStatus(InternshipStatus.CLOSED);

        InternshipOffer saved = internshipOfferRepository.save(offer);
        auditService.record(authentication, servletRequest, "OFFER_RESTORED", "INTERNSHIP_OFFER", id, offer.getTitleEn());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentlyDeleteOffer(
        @PathVariable Long id,
        Authentication authentication,
        HttpServletRequest servletRequest
    ) {
        InternshipOffer offer = internshipOfferRepository
            .findById(id)
            .orElse(null);

        if (offer == null || !offer.isArchived()) {
            return ResponseEntity.notFound().build();
        }

        long applicationCount = internshipApplicationRepository
            .countByInternshipOfferId(id);

        if (applicationCount > 0) {
            return ResponseEntity.badRequest().body(
                Map.of(
                    "message",
                    "This offer cannot be permanently deleted because it has "
                        + applicationCount
                        + (applicationCount == 1 ? " applicant." : " applicants.")
                )
            );
        }

        internshipOfferRepository.delete(offer);
        auditService.record(authentication, servletRequest, "OFFER_PERMANENTLY_DELETED", "INTERNSHIP_OFFER", id, offer.getTitleEn());

        return ResponseEntity.ok(
            Map.of("message", "Internship offer permanently deleted.")
        );
    }


    /* =========================
       GET ONE
    ========================= */

    @GetMapping("/{id}")
    public ResponseEntity<?> getOffer(
        @PathVariable Long id
    ) {

        return internshipOfferRepository
            .findByIdAndArchivedFalse(id)
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
        @RequestBody InternshipOfferRequest request,
        Authentication authentication,
        HttpServletRequest servletRequest
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

        InternshipStatus requestedStatus = request.status() == null
            ? InternshipStatus.OPEN
            : request.status();

        if (
            requestedStatus == InternshipStatus.OPEN &&
            request.applicationDeadline().isBefore(LocalDate.now())
        ) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "An open offer cannot have a deadline in the past.")
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

        auditService.record(authentication, servletRequest, "OFFER_CREATED", "INTERNSHIP_OFFER", saved.getId(), saved.getTitleEn());


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
        @RequestBody InternshipOfferRequest request,
        Authentication authentication,
        HttpServletRequest servletRequest
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
                .findByIdAndArchivedFalse(id)
                .orElse(null);


        if (offer == null) {

            return ResponseEntity
                .notFound()
                .build();
        }

        int currentCandidates = offer.getCurrentCandidates() == null
            ? 0
            : offer.getCurrentCandidates();

        if (request.maxCandidates() < currentCandidates) {
            return ResponseEntity.badRequest().body(
                Map.of(
                    "message",
                    "Maximum candidates cannot be lower than the current applicant count."
                )
            );
        }

        InternshipStatus resultingStatus = request.status() == null
            ? offer.getStatus()
            : request.status();

        if (
            resultingStatus == InternshipStatus.OPEN &&
            request.applicationDeadline().isBefore(LocalDate.now())
        ) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "An open offer cannot have a deadline in the past.")
            );
        }

        if (
            resultingStatus == InternshipStatus.OPEN &&
            currentCandidates >= request.maxCandidates()
        ) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "A full offer cannot remain open.")
            );
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

        auditService.record(authentication, servletRequest, "OFFER_EDITED", "INTERNSHIP_OFFER", id, saved.getTitleEn());


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
        @RequestBody Map<String, String> body,
        Authentication authentication,
        HttpServletRequest servletRequest
    ) {

        InternshipOffer offer =
            internshipOfferRepository
                .findByIdAndArchivedFalse(id)
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

        if (
            status == InternshipStatus.OPEN &&
            offer.getApplicationDeadline() != null &&
            offer.getApplicationDeadline().isBefore(LocalDate.now())
        ) {
            return ResponseEntity.badRequest().body(
                Map.of(
                    "message",
                    "This offer cannot be reopened because its application deadline has passed."
                )
            );
        }


        InternshipStatus previousStatus = offer.getStatus();

        offer.setStatus(
            status
        );


        InternshipOffer saved =
            internshipOfferRepository
                .save(offer);

        auditService.record(
            authentication,
            servletRequest,
            "OFFER_STATUS_CHANGED",
            "INTERNSHIP_OFFER",
            id,
            previousStatus + " -> " + status
        );


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
