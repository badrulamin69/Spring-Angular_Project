package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AcademicCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AcademicCalendarRepository extends JpaRepository<AcademicCalendar, Long> {
    List<AcademicCalendar> findBySemester_Id(Long semesterId);
    List<AcademicCalendar> findByEventType(String eventType);
    List<AcademicCalendar> findByIsHoliday(boolean isHoliday);
    List<AcademicCalendar> findByStartDateBetween(LocalDate start, LocalDate end);
    List<AcademicCalendar> findBySemester_IdAndIsPublishedTrue(Long semesterId);
    List<AcademicCalendar> findBySemester_IdAndEventType(Long semesterId, String eventType);
    List<AcademicCalendar> findByStartDateGreaterThanEqualOrderByStartDateAsc(LocalDate date);
    List<AcademicCalendar> findBySemester_IdAndIsHolidayTrue(Long semesterId);
}
