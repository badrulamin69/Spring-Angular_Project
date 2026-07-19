package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.EligibilityVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EligibilityVerificationRepository extends JpaRepository<EligibilityVerification, Long> {
    List<EligibilityVerification> findByTestId(Long testId);
    List<EligibilityVerification> findByRegistrationId(Long registrationId);
    Optional<EligibilityVerification> findByTestIdAndRegistrationId(Long testId, Long registrationId);
    List<EligibilityVerification> findByTestIdAndStatus(Long testId, String status);
    long countByTestId(Long testId);
    long countByTestIdAndStatus(Long testId, String status);
    boolean existsByTestIdAndRegistrationId(Long testId, Long registrationId);
}
