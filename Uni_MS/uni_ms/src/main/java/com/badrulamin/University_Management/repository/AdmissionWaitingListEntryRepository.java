package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionWaitingListEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionWaitingListEntryRepository extends JpaRepository<AdmissionWaitingListEntry, Long> {
    Page<AdmissionWaitingListEntry> findByWaitingList_Id(Long waitingListId, Pageable pageable);
    List<AdmissionWaitingListEntry> findByWaitingList_IdOrderByRankAsc(Long waitingListId);
    List<AdmissionWaitingListEntry> findByRegistration_Id(Long registrationId);
    List<AdmissionWaitingListEntry> findByWaitingList_IdAndStatus(Long waitingListId, String status);
    long countByWaitingList_Id(Long waitingListId);
    long countByWaitingList_IdAndStatus(Long waitingListId, String status);

    @Query("SELECT e FROM AdmissionWaitingListEntry e WHERE " +
           "e.waitingList.id = :waitingListId " +
           "AND (:search IS NULL OR LOWER(e.applicantName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(e.rollNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR e.status = :status)")
    Page<AdmissionWaitingListEntry> findByFilters(
            @Param("waitingListId") Long waitingListId,
            @Param("search") String search,
            @Param("status") String status,
            Pageable pageable);
}
