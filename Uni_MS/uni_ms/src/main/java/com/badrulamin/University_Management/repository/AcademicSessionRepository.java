package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AcademicSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicSessionRepository extends JpaRepository<AcademicSession, Long> {
    Optional<AcademicSession> findByCode(String code);
    boolean existsByCode(String code);
    List<AcademicSession> findByIsActiveTrue();
    List<AcademicSession> findByStatus(String status);
}
