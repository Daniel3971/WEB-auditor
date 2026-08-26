package com.webauditor.backend.CompanyService;

import com.webauditor.backend.entity.ApplicationStatus;
import com.webauditor.backend.entity.InternshipApplication;
import com.webauditor.backend.entity.InternshipOffer;
import com.webauditor.backend.entity.InternshipStatus;

import com.webauditor.backend.repository.InternshipApplicationRepository;
import com.webauditor.backend.repository.InternshipOfferRepository;
import com.webauditor.backend.dto.AdminApplicationResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.LocalDate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.UUID;
import java.util.List;

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
    private final Path uploadDirectory;


    public InternshipApplicationService(
        InternshipApplicationRepository applicationRepository,
        InternshipOfferRepository offerRepository,
        @Value("${app.storage.cv-directory:uploads/cvs}") String cvDirectory
    ) {

        this.applicationRepository =
            applicationRepository;

        this.offerRepository =
            offerRepository;

        this.uploadDirectory = Paths.get(cvDirectory)
            .toAbsolutePath()
            .normalize();
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
                .findActiveByIdForUpdate(offerId)
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

        if (
            offer.getApplicationDeadline() != null &&
            offer.getApplicationDeadline().isBefore(LocalDate.now())
        ) {
            offer.setStatus(InternshipStatus.CLOSED);
            offerRepository.save(offer);

            throw new IllegalStateException(
                "The application deadline for this internship has passed."
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
            safeOriginalFileName(cv.getOriginalFilename());


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

    @Transactional(readOnly = true)
    public List<AdminApplicationResponse> getAllForAdmin() {
        return applicationRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
            )
            .stream()
            .map(AdminApplicationResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public long countForAdmin() {
        return applicationRepository.count();
    }

    @Transactional
    public AdminApplicationResponse updateStatusForAdmin(
        Long id,
        ApplicationStatus status
    ) {
        if (status == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Application status is required."
            );
        }

        InternshipApplication application = findApplication(id);
        application.setApplicationStatus(status);
        return AdminApplicationResponse.from(
            applicationRepository.save(application)
        );
    }

    @Transactional(readOnly = true)
    public AdminApplicationResponse getForAdmin(Long id) {
        return AdminApplicationResponse.from(findApplication(id));
    }

    @Transactional(readOnly = true)
    public List<AdminApplicationResponse> getByOfferForAdmin(Long offerId) {
        return applicationRepository.findByInternshipOfferId(offerId)
            .stream()
            .map(AdminApplicationResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public StoredCv getCvForAdmin(Long id) {
        InternshipApplication application = findApplication(id);
        Path storedPath = Paths.get(application.getCvFilePath())
            .toAbsolutePath()
            .normalize();

        if (!storedPath.startsWith(uploadDirectory)) {
            throw new IllegalStateException("Invalid CV storage path.");
        }

        if (!Files.isRegularFile(storedPath) || !Files.isReadable(storedPath)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "CV file is unavailable."
            );
        }

        try {
            return new StoredCv(
                storedPath,
                safeOriginalFileName(application.getCvFileName()),
                Files.size(storedPath)
            );
        } catch (IOException exception) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "CV file is unavailable.",
                exception
            );
        }
    }

    private InternshipApplication findApplication(Long id) {
        return applicationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Application not found."
            ));
    }

    public record StoredCv(Path path, String originalFileName, long size) {}


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
    ) throws IOException {


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

        byte[] signature = new byte[5];
        try (var input = cv.getInputStream()) {
            if (input.read(signature) != signature.length ||
                signature[0] != '%' || signature[1] != 'P' ||
                signature[2] != 'D' || signature[3] != 'F' ||
                signature[4] != '-') {
                throw new IllegalArgumentException("The uploaded file is not a valid PDF.");
            }
        }
    }

    private String safeOriginalFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "cv.pdf";
        }

        String normalized = fileName.replace('\\', '/');
        String baseName = normalized.substring(normalized.lastIndexOf('/') + 1)
            .replaceAll("[\\r\\n\\\"]", "_")
            .trim();

        if (baseName.isBlank()) {
            return "cv.pdf";
        }

        return baseName.length() > 200
            ? baseName.substring(baseName.length() - 200)
            : baseName;
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
