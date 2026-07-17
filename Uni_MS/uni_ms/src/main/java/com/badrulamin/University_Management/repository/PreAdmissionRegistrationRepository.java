package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.PreAdmissionRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PreAdmissionRegistrationRepository extends JpaRepository<PreAdmissionRegistration, Long> {
    Optional<PreAdmissionRegistration> findByRegistrationNumber(String registrationNumber);
    long countByStatus(String status);
    Page<PreAdmissionRegistration> findByStatus(String status, Pageable pageable);
    List<PreAdmissionRegistration> findByStatusIn(Collection<String> statuses);
    Page<PreAdmissionRegistration> findBySession_Id(Long sessionId, Pageable pageable);
    boolean existsByEmail(String email);
    Optional<PreAdmissionRegistration> findByEmail(String email);

    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING_INDEX(registration_number, '-', -1) AS UNSIGNED)), 0) FROM pre_admission_registration WHERE registration_number LIKE CONCAT(:prefix, '%')", nativeQuery = true)
    long findMaxSequenceByRegistrationNumberPrefix(@Param("prefix") String prefix);
}
