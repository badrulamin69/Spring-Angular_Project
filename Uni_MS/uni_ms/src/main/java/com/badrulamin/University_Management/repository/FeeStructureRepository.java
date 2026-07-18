package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findBySemester_IdAndProgram_IdAndIsActiveTrue(Long semesterId, Long programId);
    List<FeeStructure> findByProgram_IdAndAcademicYearAndIsActiveTrue(Long programId, String academicYear);
    List<FeeStructure> findByFeeType_IdAndIsActiveTrue(Long feeTypeId);
    List<FeeStructure> findByIsActiveTrue();
}
