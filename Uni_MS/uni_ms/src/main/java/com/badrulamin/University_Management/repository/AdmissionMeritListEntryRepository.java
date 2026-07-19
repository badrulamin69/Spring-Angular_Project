package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionMeritListEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionMeritListEntryRepository extends JpaRepository<AdmissionMeritListEntry, Long> {
    Page<AdmissionMeritListEntry> findByMeritList_Id(Long meritListId, Pageable pageable);
    List<AdmissionMeritListEntry> findByMeritList_IdOrderByRankAsc(Long meritListId);
    List<AdmissionMeritListEntry> findByRegistration_Id(Long registrationId);
    List<AdmissionMeritListEntry> findByRegistration_IdAndStatusIn(Long registrationId, java.util.Collection<String> statuses);
    List<AdmissionMeritListEntry> findByMeritList_IdAndStatus(Long meritListId, String status);
    long countByMeritList_Id(Long meritListId);
    long countByMeritList_IdAndStatus(Long meritListId, String status);
    boolean existsByMeritList_IdAndRegistration_Id(Long meritListId, Long registrationId);

    @Query("SELECT e FROM AdmissionMeritListEntry e WHERE " +
           "e.meritList.id = :meritListId " +
           "AND (:search IS NULL OR LOWER(e.applicantName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(e.rollNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(e.applicationNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR e.status = :status) " +
           "AND (:quotaType IS NULL OR e.quotaType = :quotaType)")
    Page<AdmissionMeritListEntry> findByFilters(
            @Param("meritListId") Long meritListId,
            @Param("search") String search,
            @Param("status") String status,
            @Param("quotaType") String quotaType,
            Pageable pageable);
}
