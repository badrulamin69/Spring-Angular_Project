package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.SemesterRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRegistrationRepository extends JpaRepository<SemesterRegistration, Long> {
    Page<SemesterRegistration> findByStudentId(Long studentId, Pageable pageable);
    long countByStatus(String status);
}
