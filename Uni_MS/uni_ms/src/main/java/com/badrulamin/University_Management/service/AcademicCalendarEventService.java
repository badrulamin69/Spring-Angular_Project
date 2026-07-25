package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AcademicCalendarEvent;
import com.badrulamin.University_Management.entity.AcademicSession;
import com.badrulamin.University_Management.entity.Semester;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.request.AcademicCalendarEventRequest;
import com.badrulamin.University_Management.payload.response.AcademicCalendarEventResponse;
import com.badrulamin.University_Management.repository.AcademicCalendarEventRepository;
import com.badrulamin.University_Management.repository.AcademicSessionRepository;
import com.badrulamin.University_Management.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcademicCalendarEventService {

    private final AcademicCalendarEventRepository academicCalendarEventRepository;
    private final SemesterRepository semesterRepository;
    private final AcademicSessionRepository academicSessionRepository;

    public Page<AcademicCalendarEventResponse> findAll(Pageable pageable) {
        return academicCalendarEventRepository.findAll(pageable).map(this::toResponse);
    }

    public AcademicCalendarEventResponse findById(Long id) {
        AcademicCalendarEvent event = academicCalendarEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicCalendarEvent", "id", id));
        return toResponse(event);
    }

    public AcademicCalendarEventResponse create(AcademicCalendarEventRequest request) {
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));

        AcademicCalendarEvent event = new AcademicCalendarEvent();
        mapRequestToEntity(event, request, semester);
        AcademicCalendarEvent saved = academicCalendarEventRepository.save(event);
        return toResponse(saved);
    }

    @Transactional
    public AcademicCalendarEventResponse update(Long id, AcademicCalendarEventRequest request) {
        AcademicCalendarEvent event = academicCalendarEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicCalendarEvent", "id", id));

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));

        mapRequestToEntity(event, request, semester);
        AcademicCalendarEvent saved = academicCalendarEventRepository.save(event);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!academicCalendarEventRepository.existsById(id)) {
            throw new ResourceNotFoundException("AcademicCalendarEvent", "id", id);
        }
        academicCalendarEventRepository.deleteById(id);
    }

    public List<AcademicCalendarEventResponse> findBySemester(Long semesterId) {
        return academicCalendarEventRepository.findBySemester_Id(semesterId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<AcademicCalendarEventResponse> findPublishedBySemester(Long semesterId) {
        return academicCalendarEventRepository.findBySemester_IdAndIsPublishedTrue(semesterId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<AcademicCalendarEventResponse> findUpcoming() {
        return academicCalendarEventRepository.findByStartDateGreaterThanEqualOrderByStartDateAsc(LocalDate.now())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<AcademicCalendarEventResponse> findHolidays(Long semesterId) {
        return academicCalendarEventRepository.findBySemester_IdAndIsHolidayTrue(semesterId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<AcademicCalendarEventResponse> findByDateRange(LocalDate start, LocalDate end) {
        return academicCalendarEventRepository.findByStartDateBetween(start, end)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private void mapRequestToEntity(AcademicCalendarEvent event, AcademicCalendarEventRequest request, Semester semester) {
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventType(request.getEventType());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setSemester(semester);
        event.setHoliday(request.isHoliday());
        event.setPublished(request.isPublished());
        event.setAllDay(request.isAllDay());
        event.setColor(request.getColor());
        event.setLocation(request.getLocation());
        event.setRecurrence(request.getRecurrence());
        event.setNotifyStudents(request.isNotifyStudents());
        event.setNotifyTeachers(request.isNotifyTeachers());

        if (request.getAcademicSessionId() != null) {
            AcademicSession academicSession = academicSessionRepository.findById(request.getAcademicSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("AcademicSession", "id", request.getAcademicSessionId()));
            event.setAcademicSession(academicSession);
        } else {
            event.setAcademicSession(null);
        }
    }

    private AcademicCalendarEventResponse toResponse(AcademicCalendarEvent event) {
        AcademicCalendarEventResponse response = new AcademicCalendarEventResponse();
        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setEventType(event.getEventType());
        response.setStartDate(event.getStartDate());
        response.setEndDate(event.getEndDate());
        response.setStartTime(event.getStartTime());
        response.setEndTime(event.getEndTime());
        response.setSemesterId(event.getSemesterId());
        response.setSemesterName(event.getSemester() != null ? event.getSemester().getName() : null);
        response.setAcademicSessionId(event.getAcademicSessionId());
        response.setAcademicSessionName(event.getAcademicSession() != null ? event.getAcademicSession().getName() : null);
        response.setHoliday(event.isHoliday());
        response.setPublished(event.isPublished());
        response.setAllDay(event.isAllDay());
        response.setColor(event.getColor());
        response.setLocation(event.getLocation());
        response.setRecurrence(event.getRecurrence());
        response.setNotifyStudents(event.isNotifyStudents());
        response.setNotifyTeachers(event.isNotifyTeachers());
        response.setCreatedAt(event.getCreatedAt());
        response.setUpdatedAt(event.getUpdatedAt());
        return response;
    }
}