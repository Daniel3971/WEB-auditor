package com.webauditor.backend.repository;

import com.webauditor.backend.entity.InternshipOffer;
import com.webauditor.backend.entity.InternshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipOfferRepository
        extends JpaRepository<InternshipOffer, Long> {

    List<InternshipOffer> findByStatus(InternshipStatus status);
}