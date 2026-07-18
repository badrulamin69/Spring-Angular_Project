package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionTestAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionTestAttemptRepository extends JpaRepository<AdmissionTestAttempt, Long> {
    Optional<AdmissionTestAttempt> findByRegistration_IdAndTest_Id(Long registrationId, Long testId);
    List<AdmissionTestAttempt> findByRegistration_Id(Long registrationId);
    Page<AdmissionTestAttempt> findByTest_Id(Long testId, Pageable pageable);
    boolean existsByRegistration_IdAndTest_IdAndStatusIn(Long registrationId, Long testId, List<String> statuses);
}
