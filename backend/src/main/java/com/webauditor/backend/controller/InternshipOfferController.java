package com.webauditor.backend.controller;

import com.webauditor.backend.entity.InternshipOffer;
import com.webauditor.backend.entity.InternshipStatus;
import com.webauditor.backend.repository.InternshipOfferRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internship-offers")
@CrossOrigin(origins = "http://localhost:4200")
public class InternshipOfferController {

    private final InternshipOfferRepository repository;

    public InternshipOfferController(
        InternshipOfferRepository repository
    ) {
        this.repository = repository;
    }

    @GetMapping
    public List<InternshipOffer> getAllOffers() {
        return repository.findAll();
    }

    @GetMapping("/open")
    public List<InternshipOffer> getOpenOffers() {
        return repository.findByStatus(
            InternshipStatus.OPEN
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternshipOffer> getOfferById(
        @PathVariable Long id
    ) {
        return repository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public InternshipOffer createOffer(
        @RequestBody InternshipOffer offer
    ) {
        offer.setId(null);

        if (offer.getStatus() == null) {
            offer.setStatus(InternshipStatus.OPEN);
        }

        return repository.save(offer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InternshipOffer> updateOffer(
        @PathVariable Long id,
        @RequestBody InternshipOffer updatedOffer
    ) {
        return repository.findById(id)
            .map(existingOffer -> {

                existingOffer.setTitleEn(
                    updatedOffer.getTitleEn()
                );

                existingOffer.setTitleAr(
                    updatedOffer.getTitleAr()
                );

                existingOffer.setDescriptionEn(
                    updatedOffer.getDescriptionEn()
                );

                existingOffer.setDescriptionAr(
                    updatedOffer.getDescriptionAr()
                );

                existingOffer.setMissionEn(
                    updatedOffer.getMissionEn()
                );

                existingOffer.setMissionAr(
                    updatedOffer.getMissionAr()
                );

                existingOffer.setProfileEn(
                    updatedOffer.getProfileEn()
                );

                existingOffer.setProfileAr(
                    updatedOffer.getProfileAr()
                );

                existingOffer.setDuration(
                    updatedOffer.getDuration()
                );

                existingOffer.setApplicationDeadline(
                    updatedOffer.getApplicationDeadline()
                );

                existingOffer.setMaxCandidates(
                    updatedOffer.getMaxCandidates()
                );

                existingOffer.setCurrentCandidates(
                    updatedOffer.getCurrentCandidates()
                );

                existingOffer.setStatus(
                    updatedOffer.getStatus()
                );

                return ResponseEntity.ok(
                    repository.save(existingOffer)
                );
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InternshipOffer> changeStatus(
        @PathVariable Long id,
        @RequestParam InternshipStatus status
    ) {
        return repository.findById(id)
            .map(offer -> {
                offer.setStatus(status);

                return ResponseEntity.ok(
                    repository.save(offer)
                );
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(
        @PathVariable Long id
    ) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}