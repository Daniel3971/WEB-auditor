package com.webauditor.backend.CompanyService;

import com.webauditor.backend.entity.ApplicationStatus;
import com.webauditor.backend.entity.InternshipApplication;
import com.webauditor.backend.entity.InternshipOffer;
import com.webauditor.backend.entity.InternshipStatus;
import com.webauditor.backend.repository.InternshipApplicationRepository;
import com.webauditor.backend.repository.InternshipOfferRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class InternshipApplicationService {

    private final InternshipApplicationRepository
        applicationRepository;

    private final InternshipOfferRepository
        offerRepository;

    private final Path uploadDirectory =
        Paths.get("uploads", "cvs");

    public InternshipApplicationService(
        InternshipApplicationRepository
            applicationRepository,
        InternshipOfferRepository
            offerRepository
    ) {
        this.applicationRepository =
            applicationRepository;

        this.offerRepository =
            offerRepository;
    }

    @Transactional
    public InternshipApplication apply(
        Long offerId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String university,
        String major,
        String message,
        MultipartFile cv
    ) throws IOException {

        InternshipOffer offer =
            offerRepository
                .findById(offerId)
                .orElseThrow(
                    () -> new IllegalArgumentException(
                        "Internship offer not found."
                    )
                );

        if (
            offer.getStatus() !=
            InternshipStatus.OPEN
        ) {
            throw new IllegalStateException(
                "This internship offer is closed."
            );
        }

        int currentCandidates =
            offer.getCurrentCandidates() == null
                ? 0
                : offer.getCurrentCandidates();

        int maxCandidates =
            offer.getMaxCandidates() == null
                ? 0
                : offer.getMaxCandidates();

        if (
            maxCandidates > 0 &&
            currentCandidates >= maxCandidates
        ) {
            offer.setStatus(
                InternshipStatus.CLOSED
            );

            offerRepository.save(offer);

            throw new IllegalStateException(
                "This internship offer is full."
            );
        }

        if (
            cv == null ||
            cv.isEmpty()
        ) {
            throw new IllegalArgumentException(
                "CV is required."
            );
        }

        String originalFileName =
            cv.getOriginalFilename();

        if (
            originalFileName == null ||
            !originalFileName
                .toLowerCase()
                .endsWith(".pdf")
        ) {
            throw new IllegalArgumentException(
                "Only PDF files are allowed."
            );
        }

        long maxFileSize =
            5L * 1024L * 1024L;

        if (
            cv.getSize() >
            maxFileSize
        ) {
            throw new IllegalArgumentException(
                "CV must be smaller than 5 MB."
            );
        }

        Files.createDirectories(
            uploadDirectory
        );

        String storedFileName =
            UUID.randomUUID()
                + ".pdf";

        Path targetPath =
            uploadDirectory.resolve(
                storedFileName
            );

        Files.copy(
            cv.getInputStream(),
            targetPath,
            StandardCopyOption.REPLACE_EXISTING
        );

        InternshipApplication application =
            new InternshipApplication();

        application.setInternshipOffer(
            offer
        );

        application.setFirstName(
            firstName
        );

        application.setLastName(
            lastName
        );

        application.setEmail(
            email
        );

        application.setPhone(
            phone
        );

        application.setUniversity(
            university
        );

        application.setMajor(
            major
        );

        application.setMessage(
            message
        );

        application.setCvFileName(
            originalFileName
        );

        application.setCvFilePath(
            targetPath
                .toAbsolutePath()
                .toString()
        );

        application.setApplicationStatus(
            ApplicationStatus.PENDING
        );

        InternshipApplication saved =
            applicationRepository.save(
                application
            );

        currentCandidates++;

        offer.setCurrentCandidates(
            currentCandidates
        );

        if (
            maxCandidates > 0 &&
            currentCandidates >= maxCandidates
        ) {
            offer.setStatus(
                InternshipStatus.CLOSED
            );
        }

        offerRepository.save(
            offer
        );

        return saved;
    }
}