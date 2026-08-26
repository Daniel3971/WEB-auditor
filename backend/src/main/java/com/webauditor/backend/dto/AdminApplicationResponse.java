package com.webauditor.backend.dto;

import com.webauditor.backend.entity.ApplicationStatus;
import com.webauditor.backend.entity.InternshipApplication;

import java.time.LocalDateTime;

public record AdminApplicationResponse(
    Long id,
    Long offerId,
    String offerTitleEn,
    String offerTitleAr,
    String firstName,
    String lastName,
    String email,
    String phone,
    String university,
    String major,
    String message,
    String cvFileName,
    ApplicationStatus applicationStatus,
    LocalDateTime createdAt
) {
    public static AdminApplicationResponse from(
        InternshipApplication application
    ) {
        return new AdminApplicationResponse(
            application.getId(),
            application.getInternshipOffer().getId(),
            application.getInternshipOffer().getTitleEn(),
            application.getInternshipOffer().getTitleAr(),
            application.getFirstName(),
            application.getLastName(),
            application.getEmail(),
            application.getPhone(),
            application.getUniversity(),
            application.getMajor(),
            application.getMessage(),
            application.getCvFileName(),
            application.getApplicationStatus(),
            application.getCreatedAt()
        );
    }
}
