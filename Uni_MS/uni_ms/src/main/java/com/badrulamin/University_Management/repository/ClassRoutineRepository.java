package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.ClassRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRoutineRepository extends JpaRepository<ClassRoutine, Long> {
    List<ClassRoutine> findBySection_Id(Long sectionId);
    List<ClassRoutine> findBySemester_Id(Long semesterId);
    List<ClassRoutine> findByAdministration_Id(Long administrationId);
    List<ClassRoutine> findByDayOfWeek(String dayOfWeek);
    List<ClassRoutine> findBySection_IdAndDayOfWeek(Long sectionId, String dayOfWeek);
    List<ClassRoutine> findByClassroom_Id(Long classroomId);
    List<ClassRoutine> findByTimeSlot_Id(Long timeSlotId);
    List<ClassRoutine> findBySemester_IdAndSection_Id(Long semesterId, Long sectionId);
    List<ClassRoutine> findBySemester_IdAndDayOfWeek(Long semesterId, String dayOfWeek);
    List<ClassRoutine> findBySemester_IdAndSection_IdAndDayOfWeek(Long semesterId, Long sectionId, String dayOfWeek);
    List<ClassRoutine> findByAdministration_IdAndSemester_Id(Long administrationId, Long semesterId);
    List<ClassRoutine> findByClassroom_IdAndDayOfWeek(Long classroomId, String dayOfWeek);
    List<ClassRoutine> findByTimeSlot_IdAndDayOfWeekAndSemester_Id(Long timeSlotId, String dayOfWeek, Long semesterId);
    List<ClassRoutine> findByPublishStatus(String publishStatus);
    List<ClassRoutine> findByPublishStatusAndSemester_Id(String publishStatus, Long semesterId);
    List<ClassRoutine> findByShift(String shift);

    @Query("SELECT cr FROM ClassRoutine cr WHERE cr.classroom.id = :classroomId AND cr.dayOfWeek = :dayOfWeek AND cr.timeSlot.id = :timeSlotId AND cr.isActive = true AND cr.id <> :excludeId")
    List<ClassRoutine> findRoomConflicts(@Param("classroomId") Long classroomId, @Param("dayOfWeek") String dayOfWeek, @Param("timeSlotId") Long timeSlotId, @Param("excludeId") Long excludeId);

    @Query("SELECT cr FROM ClassRoutine cr WHERE cr.administration.id = :teacherId AND cr.dayOfWeek = :dayOfWeek AND cr.timeSlot.id = :timeSlotId AND cr.isActive = true AND cr.id <> :excludeId")
    List<ClassRoutine> findTeacherConflicts(@Param("teacherId") Long teacherId, @Param("dayOfWeek") String dayOfWeek, @Param("timeSlotId") Long timeSlotId, @Param("excludeId") Long excludeId);

    @Query("SELECT cr FROM ClassRoutine cr WHERE cr.semester.id = :semesterId AND cr.section.id = :sectionId AND cr.dayOfWeek = :dayOfWeek AND cr.timeSlot.id = :timeSlotId AND cr.isActive = true AND cr.id <> :excludeId")
    List<ClassRoutine> findStudentConflicts(@Param("semesterId") Long semesterId, @Param("sectionId") Long sectionId, @Param("dayOfWeek") String dayOfWeek, @Param("timeSlotId") Long timeSlotId, @Param("excludeId") Long excludeId);
}
