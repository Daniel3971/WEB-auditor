package com.webauditor.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "internship_offers")
public class InternshipOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title_en", nullable = false, length = 200)
    private String titleEn;

    @Column(name = "title_ar", nullable = false, length = 200)
    private String titleAr;

    @Column(name = "description_en", nullable = false, columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_ar", nullable = false, columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(name = "mission_en", columnDefinition = "TEXT")
    private String missionEn;

    @Column(name = "mission_ar", columnDefinition = "TEXT")
    private String missionAr;

    @Column(name = "profile_en", columnDefinition = "TEXT")
    private String profileEn;

    @Column(name = "profile_ar", columnDefinition = "TEXT")
    private String profileAr;

    @Column(length = 100)
    private String duration;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Column(name = "max_candidates", nullable = false)
    private Integer maxCandidates = 50;

    @Column(name = "current_candidates", nullable = false)
    private Integer currentCandidates = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InternshipStatus status = InternshipStatus.OPEN;

    @Column(nullable = false)
    private boolean archived = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = InternshipStatus.OPEN;
        }

        if (maxCandidates == null) {
            maxCandidates = 50;
        }

        if (currentCandidates == null) {
            currentCandidates = 0;
        }

        updateAutomaticStatus();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        updateAutomaticStatus();
    }

    private void updateAutomaticStatus() {
        if (
            maxCandidates != null &&
            currentCandidates != null &&
            currentCandidates >= maxCandidates
        ) {
            status = InternshipStatus.CLOSED;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionAr() {
        return descriptionAr;
    }

    public void setDescriptionAr(String descriptionAr) {
        this.descriptionAr = descriptionAr;
    }

    public String getMissionEn() {
        return missionEn;
    }

    public void setMissionEn(String missionEn) {
        this.missionEn = missionEn;
    }

    public String getMissionAr() {
        return missionAr;
    }

    public void setMissionAr(String missionAr) {
        this.missionAr = missionAr;
    }

    public String getProfileEn() {
        return profileEn;
    }

    public void setProfileEn(String profileEn) {
        this.profileEn = profileEn;
    }

    public String getProfileAr() {
        return profileAr;
    }

    public void setProfileAr(String profileAr) {
        this.profileAr = profileAr;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public Integer getMaxCandidates() {
        return maxCandidates;
    }

    public void setMaxCandidates(Integer maxCandidates) {
        this.maxCandidates = maxCandidates;
    }

    public Integer getCurrentCandidates() {
        return currentCandidates;
    }

    public void setCurrentCandidates(Integer currentCandidates) {
        this.currentCandidates = currentCandidates;
    }

    public InternshipStatus getStatus() {
        return status;
    }

    public void setStatus(InternshipStatus status) {
        this.status = status;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
