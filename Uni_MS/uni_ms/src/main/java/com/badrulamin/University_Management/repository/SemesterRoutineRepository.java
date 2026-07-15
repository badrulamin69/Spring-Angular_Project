package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.SemesterRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterRoutineRepository extends JpaRepository<SemesterRoutine, Long> {
    List<SemesterRoutine> findBySemester_Id(Long semesterId);
    List<SemesterRoutine> findByProgram_Id(Long programId);
    List<SemesterRoutine> findByBatch_Id(Long batchId);
}
