package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.CourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, Long> {
}
