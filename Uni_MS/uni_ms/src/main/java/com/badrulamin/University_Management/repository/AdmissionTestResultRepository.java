package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionTestResultRepository extends JpaRepository<AdmissionTestResult, Long> {
    Optional<AdmissionTestResult> findByRegistration_Id(Long registrationId);
    long countByStatus(String status);
    List<AdmissionTestResult> findByTest_IdOrderByTotalWeightedScoreDesc(Long testId);
    List<AdmissionTestResult> findByTest_IdAndStatus(Long testId, String status);
    boolean existsByTest_IdAndRegistration_Id(Long testId, Long registrationId);
    long countByTest_Id(Long testId);
}
