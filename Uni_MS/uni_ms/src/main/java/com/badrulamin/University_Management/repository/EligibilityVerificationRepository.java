package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.EligibilityVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EligibilityVerificationRepository extends JpaRepository<EligibilityVerification, Long> {
    List<EligibilityVerification> findByTest_Id(Long testId);
    List<EligibilityVerification> findByRegistration_Id(Long registrationId);
    Optional<EligibilityVerification> findByTest_IdAndRegistration_Id(Long testId, Long registrationId);
    List<EligibilityVerification> findByTest_IdAndStatus(Long testId, String status);
    long countByTest_Id(Long testId);
    long countByTest_IdAndStatus(Long testId, String status);
    boolean existsByTest_IdAndRegistration_Id(Long testId, Long registrationId);
}
