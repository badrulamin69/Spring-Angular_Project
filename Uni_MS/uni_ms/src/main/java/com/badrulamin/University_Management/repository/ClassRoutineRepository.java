package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.ClassRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRoutineRepository extends JpaRepository<ClassRoutine, Long> {
    List<ClassRoutine> findBySection_Id(Long sectionId);
    List<ClassRoutine> findBySemester_Id(Long semesterId);
    List<ClassRoutine> findByAdministration_Id(Long administrationId);
    List<ClassRoutine> findByDayOfWeek(String dayOfWeek);
    List<ClassRoutine> findBySection_IdAndDayOfWeek(Long sectionId, String dayOfWeek);
}
