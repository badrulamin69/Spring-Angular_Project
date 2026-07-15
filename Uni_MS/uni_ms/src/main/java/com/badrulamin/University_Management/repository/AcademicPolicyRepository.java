package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AcademicPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicPolicyRepository extends JpaRepository<AcademicPolicy, Long> {
    List<AcademicPolicy> findByPolicyType(String policyType);
    List<AcademicPolicy> findByProgram_Id(Long programId);
    List<AcademicPolicy> findByIsActiveTrue();
}
