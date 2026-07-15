package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmissionRequirementRepository extends JpaRepository<AdmissionRequirement, Long> {
}
