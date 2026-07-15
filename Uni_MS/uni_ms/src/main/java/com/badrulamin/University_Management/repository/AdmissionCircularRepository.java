package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionCircular;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdmissionCircularRepository extends JpaRepository<AdmissionCircular, Long> {
    Page<AdmissionCircular> findByStatus(String status, Pageable pageable);
    List<AdmissionCircular> findBySession_Id(Long sessionId);
    List<AdmissionCircular> findByProgram_Id(Long programId);
    List<AdmissionCircular> findByIsPublished(Boolean isPublished);
    List<AdmissionCircular> findByValidUntilAfter(LocalDate date);
    long countByStatus(String status);
}
