package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionWaitingList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionWaitingListRepository extends JpaRepository<AdmissionWaitingList, Long> {
    Page<AdmissionWaitingList> findByStatus(String status, Pageable pageable);
    List<AdmissionWaitingList> findBySession_Id(Long sessionId);
    List<AdmissionWaitingList> findByProgram_Id(Long programId);
    long countByStatus(String status);
}
