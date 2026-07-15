package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.StudentIdGeneration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentIdGenerationRepository extends JpaRepository<StudentIdGeneration, Long> {
}
