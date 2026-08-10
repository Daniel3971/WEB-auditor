package com.webauditor.backend.repository;

import com.webauditor.backend.entity.InternshipApplication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipApplicationRepository
        extends JpaRepository<InternshipApplication, Long> {

    List<InternshipApplication>
    findByInternshipOfferId(Long internshipOfferId);
}