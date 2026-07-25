package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AcademicCalendar;
import com.badrulamin.University_Management.repository.AcademicCalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcademicCalendarService {

    private final AcademicCalendarRepository academicCalendarRepository;

    public Page<AcademicCalendar> findAll(Pageable pageable) {
        return academicCalendarRepository.findAll(pageable);
    }

    public AcademicCalendar findById(Long id) {
        return academicCalendarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicCalendar", "id", id));
    }

    @Transactional
    public AcademicCalendar save(AcademicCalendar academicCalendar) {
        return academicCalendarRepository.save(academicCalendar);
    }

    @Transactional
    public AcademicCalendar update(Long id, AcademicCalendar academicCalendar) {
        findById(id);
        academicCalendar.setId(id);
        return academicCalendarRepository.save(academicCalendar);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        academicCalendarRepository.deleteById(id);
    }

    public List<AcademicCalendar> findBySemester(Long semesterId) {
        return academicCalendarRepository.findBySemester_Id(semesterId);
    }

    public List<AcademicCalendar> findHolidays(Long semesterId) {
        return academicCalendarRepository.findBySemester_IdAndIsHolidayTrue(semesterId);
    }

    public List<AcademicCalendar> findPublished(Long semesterId) {
        return academicCalendarRepository.findBySemester_IdAndIsPublishedTrue(semesterId);
    }

    private AcademicCalendar toResponse(AcademicCalendar academicCalendar) {
        return academicCalendar;
    }
}