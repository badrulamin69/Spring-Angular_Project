package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    Optional<Semester> findByCode(String code);
    boolean existsByCode(String code);
    List<Semester> findByAcademicSession_Id(Long academicSessionId);
    List<Semester> findByStatus(String status);
    List<Semester> findByIsActiveTrue();
}
