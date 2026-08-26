package com.webauditor.backend.controller;

import com.webauditor.backend.entity.InternshipOffer;
import com.webauditor.backend.entity.InternshipStatus;
import com.webauditor.backend.repository.InternshipOfferRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internship-offers")
public class InternshipOfferController {

    private final InternshipOfferRepository repository;

    public InternshipOfferController(
        InternshipOfferRepository repository
    ) {
        this.repository = repository;
    }

    @GetMapping
    public List<InternshipOffer> getAllOffers() {
        return repository.findByArchivedFalse();
    }

    @GetMapping("/open")
    public List<InternshipOffer> getOpenOffers() {
        return repository.findByStatusAndArchivedFalse(
            InternshipStatus.OPEN
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternshipOffer> getOfferById(
        @PathVariable Long id
    ) {
        return repository.findByIdAndArchivedFalse(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

}
