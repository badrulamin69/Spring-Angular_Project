package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionEnrollmentRepository extends JpaRepository<AdmissionEnrollment, Long> {
    Page<AdmissionEnrollment> findByStatus(String status, Pageable pageable);
    List<AdmissionEnrollment> findByApplication_Id(Long applicationId);
    List<AdmissionEnrollment> findByStudent_Id(Long studentId);
    Optional<AdmissionEnrollment> findByEnrollmentNumber(String enrollmentNumber);
    long countByStatus(String status);
    long countByProgram_Id(Long programId);
    long countByBatch_Id(Long batchId);
}
