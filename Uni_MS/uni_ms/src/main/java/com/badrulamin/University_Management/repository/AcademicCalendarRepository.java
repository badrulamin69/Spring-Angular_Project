package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AcademicCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AcademicCalendarRepository extends JpaRepository<AcademicCalendar, Long> {
    List<AcademicCalendar> findBySemester_id(Long semesterId);
    List<AcademicCalendar> findByEventType(String eventType);
    List<AcademicCalendar> findByIsHoliday(boolean isHoliday);
    List<AcademicCalendar> findByStartDateBetween(LocalDate start, LocalDate end);
}
