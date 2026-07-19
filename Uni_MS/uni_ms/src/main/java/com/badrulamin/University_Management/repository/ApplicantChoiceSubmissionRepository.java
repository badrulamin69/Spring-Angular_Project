package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.ApplicantChoiceSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicantChoiceSubmissionRepository extends JpaRepository<ApplicantChoiceSubmission, Long> {

    Optional<ApplicantChoiceSubmission> findByRegistration_IdAndConfig_Id(Long registrationId, Long configId);

    List<ApplicantChoiceSubmission> findByRegistration_Id(Long registrationId);

    Optional<ApplicantChoiceSubmission> findBySubmissionId(String submissionId);

    boolean existsByRegistration_IdAndConfig_IdAndStatusIn(Long registrationId, Long configId, List<String> statuses);

    @Query("SELECT s FROM ApplicantChoiceSubmission s WHERE " +
           "(:search IS NULL OR LOWER(s.applicantName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.submissionId) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR s.status = :status) " +
           "AND (:configId IS NULL OR s.config.id = :configId)")
    Page<ApplicantChoiceSubmission> findByFilters(@Param("search") String search,
                                                  @Param("status") String status,
                                                  @Param("configId") Long configId,
                                                  Pageable pageable);

    long countByConfig_IdAndStatus(Long configId, String status);

    long countByConfig_Id(Long configId);

    List<ApplicantChoiceSubmission> findByConfig_IdAndStatus(Long configId, String status);
}
