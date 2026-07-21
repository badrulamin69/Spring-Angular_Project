package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AcademicCalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AcademicCalendarEventRepository extends JpaRepository<AcademicCalendarEvent, Long> {
    List<AcademicCalendarEvent> findBySemester_Id(Long semesterId);
    List<AcademicCalendarEvent> findBySemester_IdAndIsPublishedTrue(Long semesterId);
    List<AcademicCalendarEvent> findByEventType(String eventType);
    List<AcademicCalendarEvent> findByIsHoliday(boolean isHoliday);
    List<AcademicCalendarEvent> findByStartDateBetween(LocalDate start, LocalDate end);
    List<AcademicCalendarEvent> findBySemester_IdAndEventType(Long semesterId, String eventType);
    List<AcademicCalendarEvent> findByStartDateGreaterThanEqualOrderByStartDateAsc(LocalDate date);
    List<AcademicCalendarEvent> findBySemester_IdAndIsHolidayTrue(Long semesterId);
}
