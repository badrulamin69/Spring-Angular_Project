package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.CourseRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {
    Page<CourseRegistration> findByStudent_Id(Long studentId, Pageable pageable);
    List<CourseRegistration> findBySemester_Id(Long semesterId);
    long countByStatus(String status);
    long countByStudent_IdAndSemester_Id(Long studentId, Long semesterId);
}
