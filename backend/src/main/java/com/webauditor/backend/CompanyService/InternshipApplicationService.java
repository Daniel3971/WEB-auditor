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


    /*
     * CVs are stored here locally.
     *
     * backend/
     *   uploads/
     *     cvs/
     */
    private final Path uploadDirectory =
        Paths.get(
            "uploads",
            "cvs"
        );


    public InternshipApplicationService(
        InternshipApplicationRepository applicationRepository,
        InternshipOfferRepository offerRepository
    ) {

        this.applicationRepository =
            applicationRepository;

        this.offerRepository =
            offerRepository;
    }


    /* =========================================
       CREATE APPLICATION
    ========================================== */

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


        /* =====================================
           FIND OFFER
        ====================================== */

        InternshipOffer offer =
            offerRepository
                .findById(offerId)
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "Internship offer not found."
                        )
                );


        /* =====================================
           CHECK OFFER STATUS
        ====================================== */

        if (
            offer.getStatus() !=
            InternshipStatus.OPEN
        ) {

            throw new IllegalStateException(
                "This internship offer is closed."
            );
        }


        /* =====================================
           VALIDATE CANDIDATE DATA
        ====================================== */

        validateApplication(
            firstName,
            lastName,
            email,
            phone,
            university,
            major,
            message
        );


        /*
         * Normalize important values.
         */

        firstName =
            firstName.trim();

        lastName =
            lastName.trim();

        email =
            email.trim()
                .toLowerCase();

        phone =
            phone.trim();


        if (university != null) {
            university =
                university.trim();
        }

        if (major != null) {
            major =
                major.trim();
        }

        if (message != null) {
            message =
                message.trim();
        }


        /* =====================================
           PREVENT DUPLICATE APPLICATION
        ====================================== */

        boolean alreadyApplied =
            applicationRepository
                .existsByInternshipOfferIdAndEmailIgnoreCase(
                    offerId,
                    email
                );


        if (alreadyApplied) {

            throw new IllegalStateException(
                "You have already applied for this internship."
            );
        }


        /* =====================================
           CHECK CAPACITY
        ====================================== */

        int currentCandidates =
            offer.getCurrentCandidates() == null
                ? 0
                : offer.getCurrentCandidates();


        int maxCandidates =
            offer.getMaxCandidates() == null
                ? 50
                : offer.getMaxCandidates();


        if (
            currentCandidates >=
            maxCandidates
        ) {

            offer.setStatus(
                InternshipStatus.CLOSED
            );

            offerRepository.save(
                offer
            );


            throw new IllegalStateException(
                "This internship offer is full."
            );
        }


        /* =====================================
           VALIDATE CV
        ====================================== */

        validateCv(cv);


        /* =====================================
           CREATE UPLOAD DIRECTORY
        ====================================== */

        Files.createDirectories(
            uploadDirectory
        );


        /* =====================================
           CREATE SAFE FILE NAME
        ====================================== */

        String originalFileName =
            cv.getOriginalFilename();


        String storedFileName =
            UUID.randomUUID()
                .toString()
                + ".pdf";


        Path targetPath =
            uploadDirectory.resolve(
                storedFileName
            );


        /* =====================================
           SAVE CV
        ====================================== */

        Files.copy(
            cv.getInputStream(),
            targetPath,
            StandardCopyOption.REPLACE_EXISTING
        );


        /* =====================================
           CREATE DATABASE APPLICATION
        ====================================== */

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
            emptyToNull(
                university
            )
        );


        application.setMajor(
            emptyToNull(
                major
            )
        );


        application.setMessage(
            emptyToNull(
                message
            )
        );


        application.setCvFileName(
            originalFileName
        );


        application.setCvFilePath(
            targetPath
                .toAbsolutePath()
                .normalize()
                .toString()
        );


        application.setApplicationStatus(
            ApplicationStatus.PENDING
        );


        InternshipApplication savedApplication =
            applicationRepository.save(
                application
            );


        /* =====================================
           INCREASE CANDIDATE COUNT
        ====================================== */

        currentCandidates++;


        offer.setCurrentCandidates(
            currentCandidates
        );


        /* =====================================
           AUTO CLOSE WHEN FULL
        ====================================== */

        if (
            currentCandidates >=
            maxCandidates
        ) {

            offer.setStatus(
                InternshipStatus.CLOSED
            );
        }


        offerRepository.save(
            offer
        );


        return savedApplication;
    }


    /* =========================================
       VALIDATE FORM
    ========================================== */

    private void validateApplication(
        String firstName,
        String lastName,
        String email,
        String phone,
        String university,
        String major,
        String message
    ) {


        /* FIRST NAME */

        if (
            firstName == null ||
            firstName.trim().length() < 2 ||
            firstName.trim().length() > 100
        ) {

            throw new IllegalArgumentException(
                "First name must contain between 2 and 100 characters."
            );
        }


        /* LAST NAME */

        if (
            lastName == null ||
            lastName.trim().length() < 2 ||
            lastName.trim().length() > 100
        ) {

            throw new IllegalArgumentException(
                "Last name must contain between 2 and 100 characters."
            );
        }


        /*
         * \p{L}
         * accepts Unicode letters,
         * including English and Arabic.
         */

        String namePattern =
            "^[\\p{L}\\s'\\-]+$";


        if (
            !firstName
                .trim()
                .matches(namePattern)
        ) {

            throw new IllegalArgumentException(
                "First name contains invalid characters."
            );
        }


        if (
            !lastName
                .trim()
                .matches(namePattern)
        ) {

            throw new IllegalArgumentException(
                "Last name contains invalid characters."
            );
        }


        /* EMAIL */

        if (
            email == null ||
            email.trim().isEmpty() ||
            email.trim().length() > 150
        ) {

            throw new IllegalArgumentException(
                "A valid email address is required."
            );
        }


        String emailPattern =
            "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+"
            + "@"
            + "[A-Za-z0-9-]+"
            + "(\\.[A-Za-z0-9-]+)+$";


        if (
            !email
                .trim()
                .matches(emailPattern)
        ) {

            throw new IllegalArgumentException(
                "Invalid email address."
            );
        }


        /* PHONE */

        if (
            phone == null ||
            phone.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                "Phone number is required."
            );
        }


        String phonePattern =
            "^[0-9+()\\s-]{7,25}$";


        if (
            !phone
                .trim()
                .matches(phonePattern)
        ) {

            throw new IllegalArgumentException(
                "Invalid phone number."
            );
        }


        /* UNIVERSITY */

        if (
            university != null &&
            university.trim().length() > 150
        ) {

            throw new IllegalArgumentException(
                "University name must not exceed 150 characters."
            );
        }


        /* MAJOR */

        if (
            major != null &&
            major.trim().length() > 150
        ) {

            throw new IllegalArgumentException(
                "Major must not exceed 150 characters."
            );
        }


        /* MESSAGE */

        if (
            message != null &&
            message.trim().length() > 1500
        ) {

            throw new IllegalArgumentException(
                "Message must not exceed 1500 characters."
            );
        }
    }


    /* =========================================
       VALIDATE CV
    ========================================== */

    private void validateCv(
        MultipartFile cv
    ) {


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
            originalFileName.isBlank()
        ) {

            throw new IllegalArgumentException(
                "Invalid CV file name."
            );
        }


        boolean pdfExtension =
            originalFileName
                .toLowerCase()
                .endsWith(".pdf");


        boolean pdfContentType =
            cv.getContentType() == null ||
            cv.getContentType()
                .equalsIgnoreCase(
                    "application/pdf"
                );


        if (
            !pdfExtension ||
            !pdfContentType
        ) {

            throw new IllegalArgumentException(
                "Only PDF files are allowed."
            );
        }


        long maximumFileSize =
            5L *
            1024L *
            1024L;


        if (
            cv.getSize() >
            maximumFileSize
        ) {

            throw new IllegalArgumentException(
                "CV must be smaller than 5 MB."
            );
        }
    }


    /* =========================================
       EMPTY STRING → NULL
    ========================================== */

    private String emptyToNull(
        String value
    ) {

        if (value == null) {
            return null;
        }


        String trimmed =
            value.trim();


        return trimmed.isEmpty()
            ? null
            : trimmed;
    }
}