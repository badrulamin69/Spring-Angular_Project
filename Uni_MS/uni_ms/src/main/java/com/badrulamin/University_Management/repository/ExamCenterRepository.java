package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.ExamCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamCenterRepository extends JpaRepository<ExamCenter, Long> {
    Optional<ExamCenter> findByCode(String code);
    boolean existsByCode(String code);
    List<ExamCenter> findByIsActiveTrue();
    long countByIsActiveTrue();
}
