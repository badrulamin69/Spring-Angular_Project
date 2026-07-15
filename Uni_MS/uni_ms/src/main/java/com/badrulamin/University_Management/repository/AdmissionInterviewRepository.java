package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionInterview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdmissionInterviewRepository extends JpaRepository<AdmissionInterview, Long> {
    Page<AdmissionInterview> findByStatus(String status, Pageable pageable);
    List<AdmissionInterview> findByApplication_Id(Long applicationId);
    List<AdmissionInterview> findByInterviewer_Id(Long interviewerId);
    List<AdmissionInterview> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);
    long countByStatus(String status);
}
