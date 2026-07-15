package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Alumni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumniRepository extends JpaRepository<Alumni, Long> {
    long countByProgram_Id(Long programId);
    boolean existsByStudent_Id(Long studentId);
}
