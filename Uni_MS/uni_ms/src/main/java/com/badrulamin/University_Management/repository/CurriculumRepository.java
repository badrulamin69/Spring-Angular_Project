package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findByProgram_Id(Long programId);
    List<Curriculum> findBySemester_Id(Long semesterId);
    List<Curriculum> findByProgram_IdAndSemester_Id(Long programId, Long semesterId);
}
