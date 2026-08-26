package com.webauditor.backend.repository;

import com.webauditor.backend.entity.InternshipOffer;
import com.webauditor.backend.entity.InternshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface InternshipOfferRepository
        extends JpaRepository<InternshipOffer, Long> {

    List<InternshipOffer> findByStatus(InternshipStatus status);
    List<InternshipOffer> findByArchivedFalse();
    List<InternshipOffer> findByArchivedTrue();
    List<InternshipOffer> findByStatusAndArchivedFalse(InternshipStatus status);
    Optional<InternshipOffer> findByIdAndArchivedFalse(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select offer from InternshipOffer offer where offer.id = :id and offer.archived = false")
    Optional<InternshipOffer> findActiveByIdForUpdate(@Param("id") Long id);
}
