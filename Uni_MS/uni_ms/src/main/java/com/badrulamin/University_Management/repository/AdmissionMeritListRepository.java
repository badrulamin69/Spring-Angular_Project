package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionMeritList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionMeritListRepository extends JpaRepository<AdmissionMeritList, Long> {
    Page<AdmissionMeritList> findByStatus(String status, Pageable pageable);
    List<AdmissionMeritList> findBySession_Id(Long sessionId);
    List<AdmissionMeritList> findByProgram_Id(Long programId);
    long countByStatus(String status);
}
