package com.webauditor.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "internship_applications")
public class InternshipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "internship_offer_id",
        nullable = false
    )
    private InternshipOffer internshipOffer;

    @Column(
        name = "first_name",
        nullable = false,
        length = 100
    )
    private String firstName;

    @Column(
        name = "last_name",
        nullable = false,
        length = 100
    )
    private String lastName;

    @Column(
        nullable = false,
        length = 150
    )
    private String email;

    @Column(
        nullable = false,
        length = 50
    )
    private String phone;

    @Column(length = 150)
    private String university;

    @Column(length = 150)
    private String major;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(
        name = "cv_file_name",
        nullable = false,
        length = 255
    )
    private String cvFileName;

    @Column(
        name = "cv_file_path",
        nullable = false,
        length = 500
    )
    private String cvFilePath;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "application_status",
        nullable = false
    )
    private ApplicationStatus applicationStatus =
        ApplicationStatus.PENDING;

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (applicationStatus == null) {
            applicationStatus =
                ApplicationStatus.PENDING;
        }
    }

    public Long getId() {
        return id;
    }

    public InternshipOffer getInternshipOffer() {
        return internshipOffer;
    }

    public void setInternshipOffer(
        InternshipOffer internshipOffer
    ) {
        this.internshipOffer = internshipOffer;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(
        String firstName
    ) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(
        String lastName
    ) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
        String email
    ) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(
        String phone
    ) {
        this.phone = phone;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(
        String university
    ) {
        this.university = university;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(
        String major
    ) {
        this.major = major;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(
        String message
    ) {
        this.message = message;
    }

    public String getCvFileName() {
        return cvFileName;
    }

    public void setCvFileName(
        String cvFileName
    ) {
        this.cvFileName = cvFileName;
    }

    public String getCvFilePath() {
        return cvFilePath;
    }

    public void setCvFilePath(
        String cvFilePath
    ) {
        this.cvFilePath = cvFilePath;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(
        ApplicationStatus applicationStatus
    ) {
        this.applicationStatus =
            applicationStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}