package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Certificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Page<Certificate> findByStudentId(Long studentId, Pageable pageable);
    Optional<Certificate> findByCertificateNumber(String certificateNumber);
    long countByStatus(String status);
    long countByCertificateType(String certificateType);
}
