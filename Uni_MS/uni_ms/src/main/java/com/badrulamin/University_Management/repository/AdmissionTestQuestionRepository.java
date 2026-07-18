package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionTestQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionTestQuestionRepository extends JpaRepository<AdmissionTestQuestion, Long> {
    List<AdmissionTestQuestion> findByTest_IdOrderByCreatedAtAsc(Long testId);
    long countByTest_Id(Long testId);
    Page<AdmissionTestQuestion> findByTest_Id(Long testId, Pageable pageable);
}
