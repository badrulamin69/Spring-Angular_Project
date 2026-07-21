package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.EnrollmentHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentHistoryRepository extends JpaRepository<EnrollmentHistory, Long> {

    Page<EnrollmentHistory> findByStudent_IdOrderByCreatedAtDesc(Long studentId, Pageable pageable);

    List<EnrollmentHistory> findBySemester_IdOrderByCreatedAtDesc(Long semesterId);

    List<EnrollmentHistory> findBySemesterEnrollment_IdOrderByCreatedAtDesc(Long semesterEnrollmentId);
}
