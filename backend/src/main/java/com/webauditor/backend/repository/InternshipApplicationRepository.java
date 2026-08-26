package com.webauditor.backend.repository;

import com.webauditor.backend.entity.InternshipApplication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipApplicationRepository
        extends JpaRepository<InternshipApplication, Long> {

    /*
     * Return all applications belonging
     * to one internship offer.
     */
    List<InternshipApplication>
        findByInternshipOfferId(
            Long internshipOfferId
        );


    /*
     * Check whether the same email
     * already applied to the same offer.
     *
     * IgnoreCase means:
     *
     * test@gmail.com
     * TEST@gmail.com
     *
     * are treated as the same email.
     */
    boolean existsByInternshipOfferIdAndEmailIgnoreCase(
        Long internshipOfferId,
        String email
    );

    long countByInternshipOfferId(Long internshipOfferId);
}
