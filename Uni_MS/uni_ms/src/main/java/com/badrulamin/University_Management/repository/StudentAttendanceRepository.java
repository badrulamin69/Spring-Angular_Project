package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.StudentAttendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {
    Page<StudentAttendance> findByStudent_Id(Long studentId, Pageable pageable);
    List<StudentAttendance> findByStudent_IdAndSemester_Id(Long studentId, Long semesterId);
    long countByStatus(String status);
    long countByStudent_Id(Long studentId);
}
