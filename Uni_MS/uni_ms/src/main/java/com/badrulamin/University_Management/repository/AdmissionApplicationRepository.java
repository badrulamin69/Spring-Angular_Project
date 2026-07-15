package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionApplicationRepository extends JpaRepository<AdmissionApplication, Long> {
    Page<AdmissionApplication> findByStatus(String status, Pageable pageable);
    List<AdmissionApplication> findBySession_Id(Long sessionId);
    List<AdmissionApplication> findByProgram_Id(Long programId);
    List<AdmissionApplication> findByApplicant_Id(Long applicantId);
    Optional<AdmissionApplication> findByApplicationNumber(String applicationNumber);
    long countByStatus(String status);
    long countBySession_Id(Long sessionId);
    long countBySession_IdAndProgram_Id(Long sessionId, Long programId);
    List<AdmissionApplication> findBySession_IdAndProgram_Id(Long sessionId, Long programId);
    List<AdmissionApplication> findByIsVerifiedFalse();

    @Query("SELECT FUNCTION('MONTH', a.createdAt) as month, COUNT(a) as count " +
           "FROM AdmissionApplication a WHERE a.createdAt >= :since " +
           "GROUP BY FUNCTION('MONTH', a.createdAt) ORDER BY month")
    List<Object[]> countByMonth(@Param("since") LocalDateTime since);

    @Query("SELECT p.name as programName, COUNT(a) as count " +
           "FROM AdmissionApplication a JOIN a.program p " +
           "GROUP BY p.name ORDER BY count DESC")
    List<Object[]> countByProgram();

    @Query("SELECT a.status as status, COUNT(a) as count " +
           "FROM AdmissionApplication a " +
           "GROUP BY a.status")
    List<Object[]> countByStatusGrouped();

    @Query("SELECT a FROM AdmissionApplication a " +
           "LEFT JOIN a.applicant u " +
           "WHERE (:search IS NULL OR :search = '' " +
           "OR LOWER(a.applicationNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR :status = '' OR a.status = :status) " +
           "AND (:programId IS NULL OR a.program.id = :programId) " +
           "AND (:sessionId IS NULL OR a.session.id = :sessionId)")
    Page<AdmissionApplication> search(@Param("search") String search,
                                       @Param("status") String status,
                                       @Param("programId") Long programId,
                                       @Param("sessionId") Long sessionId,
                                       Pageable pageable);
}
