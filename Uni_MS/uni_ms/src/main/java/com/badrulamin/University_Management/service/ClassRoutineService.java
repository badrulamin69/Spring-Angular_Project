package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.request.ClassRoutineRequest;
import com.badrulamin.University_Management.payload.response.ClassRoutineResponse;
import com.badrulamin.University_Management.payload.response.ConflictCheckResponse;
import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassRoutineService {

    private final ClassRoutineRepository classRoutineRepository;
    private final SubjectRepository subjectRepository;
    private final AdministrationRepository administrationRepository;
    private final SectionRepository sectionRepository;
    private final SemesterRepository semesterRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ClassroomRepository classroomRepository;

    public Page<ClassRoutineResponse> findAll(Pageable pageable) {
        return classRoutineRepository.findAll(pageable).map(this::toResponse);
    }

    public ClassRoutineResponse findById(Long id) {
        ClassRoutine classRoutine = classRoutineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassRoutine", "id", id));
        return toResponse(classRoutine);
    }

    @Transactional
    public ClassRoutineResponse create(ClassRoutineRequest request) {
        validateRelatedEntities(request);

        if (request.getTimeSlotId() != null && request.getClassroomId() != null && request.getDayOfWeek() != null) {
            List<ConflictCheckResponse> conflicts = checkAndBuildConflicts(
                    request.getClassroomId(),
                    request.getAdministrationId(),
                    request.getSemesterId(),
                    request.getSectionId(),
                    request.getDayOfWeek(),
                    request.getTimeSlotId(),
                    null);
            if (!conflicts.isEmpty()) {
                StringBuilder message = new StringBuilder("Conflict detected: ");
                for (ConflictCheckResponse conflict : conflicts) {
                    message.append(conflict.getConflictMessage()).append("; ");
                }
                throw new BusinessException(message.toString());
            }
        }

        ClassRoutine classRoutine = new ClassRoutine();
        classRoutine.setSubject(createSubjectProxy(request.getSubjectId()));
        classRoutine.setAdministration(createAdministrationProxy(request.getAdministrationId()));
        classRoutine.setSection(createSectionProxy(request.getSectionId()));
        classRoutine.setSemester(createSemesterProxy(request.getSemesterId()));
        if (request.getTimeSlotId() != null) {
            classRoutine.setTimeSlot(createTimeSlotProxy(request.getTimeSlotId()));
        }
        if (request.getClassroomId() != null) {
            classRoutine.setClassroom(createClassroomProxy(request.getClassroomId()));
        }
        classRoutine.setDayOfWeek(request.getDayOfWeek());
        classRoutine.setStartTime(request.getStartTime());
        classRoutine.setEndTime(request.getEndTime());
        classRoutine.setRoom(request.getRoom());
        classRoutine.setBuilding(request.getBuilding());
        classRoutine.setClassType(request.getClassType());
        classRoutine.setShift(request.getShift());
        classRoutine.setActive(request.isActive());

        ClassRoutine saved = classRoutineRepository.save(classRoutine);
        return toResponse(saved);
    }

    @Transactional
    public ClassRoutineResponse update(Long id, ClassRoutineRequest request) {
        ClassRoutine classRoutine = classRoutineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassRoutine", "id", id));

        validateRelatedEntities(request);

        if (request.getTimeSlotId() != null && request.getClassroomId() != null && request.getDayOfWeek() != null) {
            List<ConflictCheckResponse> conflicts = checkAndBuildConflicts(
                    request.getClassroomId(),
                    request.getAdministrationId(),
                    request.getSemesterId(),
                    request.getSectionId(),
                    request.getDayOfWeek(),
                    request.getTimeSlotId(),
                    id);
            if (!conflicts.isEmpty()) {
                StringBuilder message = new StringBuilder("Conflict detected: ");
                for (ConflictCheckResponse conflict : conflicts) {
                    message.append(conflict.getConflictMessage()).append("; ");
                }
                throw new BusinessException(message.toString());
            }
        }

        classRoutine.setSubject(createSubjectProxy(request.getSubjectId()));
        classRoutine.setAdministration(createAdministrationProxy(request.getAdministrationId()));
        classRoutine.setSection(createSectionProxy(request.getSectionId()));
        classRoutine.setSemester(createSemesterProxy(request.getSemesterId()));
        if (request.getTimeSlotId() != null) {
            classRoutine.setTimeSlot(createTimeSlotProxy(request.getTimeSlotId()));
        }
        if (request.getClassroomId() != null) {
            classRoutine.setClassroom(createClassroomProxy(request.getClassroomId()));
        }
        classRoutine.setDayOfWeek(request.getDayOfWeek());
        classRoutine.setStartTime(request.getStartTime());
        classRoutine.setEndTime(request.getEndTime());
        classRoutine.setRoom(request.getRoom());
        classRoutine.setBuilding(request.getBuilding());
        classRoutine.setClassType(request.getClassType());
        classRoutine.setShift(request.getShift());
        classRoutine.setActive(request.isActive());

        ClassRoutine saved = classRoutineRepository.save(classRoutine);
        return toResponse(saved);
    }

    public void delete(Long id) {
        classRoutineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassRoutine", "id", id));
        classRoutineRepository.deleteById(id);
    }

    public List<ClassRoutineResponse> findBySemesterAndSection(Long semesterId, Long sectionId) {
        semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", semesterId));
        sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", sectionId));
        return classRoutineRepository.findBySemester_IdAndSection_Id(semesterId, sectionId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ClassRoutineResponse> findByTeacherAndSemester(Long teacherId, Long semesterId) {
        administrationRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Administration", "id", teacherId));
        semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", semesterId));
        return classRoutineRepository.findByAdministration_IdAndSemester_Id(teacherId, semesterId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ClassRoutineResponse> findByDayOfWeek(Long semesterId, String dayOfWeek) {
        semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", semesterId));
        return classRoutineRepository.findBySemester_IdAndDayOfWeek(semesterId, dayOfWeek).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ConflictCheckResponse> checkConflicts(Long classroomId, Long teacherId,
                                                       Long semesterId, Long sectionId,
                                                       String dayOfWeek, Long timeSlotId,
                                                       Long excludeId) {
        return checkAndBuildConflicts(classroomId, teacherId, semesterId, sectionId, dayOfWeek, timeSlotId, excludeId);
    }

    @Transactional
    public void publishRoutine(Long semesterId, Long sectionId) {
        semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", semesterId));
        sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", sectionId));

        List<ClassRoutine> routines = classRoutineRepository.findBySemester_IdAndSection_Id(semesterId, sectionId);
        if (routines.isEmpty()) {
            throw new BusinessException("No routines found for the given semester and section");
        }

        for (ClassRoutine routine : routines) {
            routine.setPublishStatus("PUBLISHED");
            classRoutineRepository.save(routine);
        }
    }

    private void validateRelatedEntities(ClassRoutineRequest request) {
        subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
        administrationRepository.findById(request.getAdministrationId())
                .orElseThrow(() -> new ResourceNotFoundException("Administration", "id", request.getAdministrationId()));
        sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", request.getSectionId()));
        semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));
        if (request.getTimeSlotId() != null) {
            timeSlotRepository.findById(request.getTimeSlotId())
                    .orElseThrow(() -> new ResourceNotFoundException("TimeSlot", "id", request.getTimeSlotId()));
        }
        if (request.getClassroomId() != null) {
            classroomRepository.findById(request.getClassroomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", request.getClassroomId()));
        }
    }

    private List<ConflictCheckResponse> checkAndBuildConflicts(Long classroomId, Long teacherId,
                                                                Long semesterId, Long sectionId,
                                                                String dayOfWeek, Long timeSlotId,
                                                                Long excludeId) {
        List<ConflictCheckResponse> conflicts = new ArrayList<>();

        if (classroomId != null && dayOfWeek != null && timeSlotId != null) {
            List<ClassRoutine> roomConflicts = classRoutineRepository.findRoomConflicts(classroomId, dayOfWeek, timeSlotId, excludeId);
            for (ClassRoutine cr : roomConflicts) {
                ConflictCheckResponse conflict = new ConflictCheckResponse();
                conflict.setHasConflict(true);
                conflict.setConflictType("ROOM_CONFLICT");
                conflict.setConflictMessage("Classroom is already occupied at this time slot");
                conflict.setConflictingRoutineId(cr.getId());
                Classroom classroom = classroomRepository.findById(classroomId).orElse(null);
                conflict.setConflictingDetails("Classroom: " + (classroom != null ? classroom.getRoomNumber() : "Unknown")
                        + " on " + dayOfWeek + " " + cr.getStartTime() + "-" + cr.getEndTime());
                conflicts.add(conflict);
            }
        }

        if (teacherId != null && dayOfWeek != null && timeSlotId != null) {
            List<ClassRoutine> teacherConflicts = classRoutineRepository.findTeacherConflicts(teacherId, dayOfWeek, timeSlotId, excludeId);
            for (ClassRoutine cr : teacherConflicts) {
                ConflictCheckResponse conflict = new ConflictCheckResponse();
                conflict.setHasConflict(true);
                conflict.setConflictType("TEACHER_CONFLICT");
                conflict.setConflictMessage("Teacher is already assigned at this time slot");
                conflict.setConflictingRoutineId(cr.getId());
                Administration teacher = administrationRepository.findById(teacherId).orElse(null);
                conflict.setConflictingDetails("Teacher: " + (teacher != null ? teacher.getFirstName() + " " + teacher.getLastName() : "Unknown")
                        + " on " + dayOfWeek + " " + cr.getStartTime() + "-" + cr.getEndTime());
                conflicts.add(conflict);
            }
        }

        if (semesterId != null && sectionId != null && dayOfWeek != null && timeSlotId != null) {
            List<ClassRoutine> studentConflicts = classRoutineRepository.findStudentConflicts(semesterId, sectionId, dayOfWeek, timeSlotId, excludeId);
            for (ClassRoutine cr : studentConflicts) {
                ConflictCheckResponse conflict = new ConflictCheckResponse();
                conflict.setHasConflict(true);
                conflict.setConflictType("STUDENT_CONFLICT");
                conflict.setConflictMessage("Section already has a class at this time slot");
                conflict.setConflictingRoutineId(cr.getId());
                Section section = sectionRepository.findById(sectionId).orElse(null);
                conflict.setConflictingDetails("Section: " + (section != null ? section.getName() : "Unknown")
                        + " on " + dayOfWeek + " " + cr.getStartTime() + "-" + cr.getEndTime());
                conflicts.add(conflict);
            }
        }

        return conflicts;
    }

    private ClassRoutineResponse toResponse(ClassRoutine cr) {
        ClassRoutineResponse response = new ClassRoutineResponse();
        response.setId(cr.getId());
        response.setDayOfWeek(cr.getDayOfWeek());
        response.setStartTime(cr.getStartTime());
        response.setEndTime(cr.getEndTime());
        response.setRoom(cr.getRoom());
        response.setBuilding(cr.getBuilding());
        response.setClassType(cr.getClassType());
        response.setShift(cr.getShift());
        response.setActive(cr.isActive());
        response.setCreatedAt(cr.getCreatedAt());
        response.setUpdatedAt(cr.getUpdatedAt());

        if (cr.getSubject() != null) {
            Subject subject = subjectRepository.findById(cr.getSubject().getId()).orElse(null);
            if (subject != null) {
                response.setSubjectId(subject.getId());
                response.setSubjectName(subject.getName());
                response.setSubjectCode(subject.getCode());
                if (subject.getDepartment() != null) {
                    Department dept = subject.getDepartment();
                    response.setDepartmentName(dept.getName());
                }
            }
        }

        if (cr.getAdministration() != null) {
            Administration admin = administrationRepository.findById(cr.getAdministration().getId()).orElse(null);
            if (admin != null) {
                response.setAdministrationId(admin.getId());
                response.setTeacherName(admin.getFirstName() + " " + admin.getLastName());
            }
        }

        if (cr.getSection() != null) {
            Section section = sectionRepository.findById(cr.getSection().getId()).orElse(null);
            if (section != null) {
                response.setSectionId(section.getId());
                response.setSectionName(section.getName());
                if (section.getBatch() != null) {
                    Batch batch = section.getBatch();
                    response.setBatchId(batch.getId());
                    response.setBatchName(batch.getName());
                    if (batch.getCourse() != null && batch.getCourse().getProgram() != null) {
                        response.setProgramName(batch.getCourse().getProgram().getName());
                    }
                }
            }
        }

        if (cr.getSemester() != null) {
            Semester semester = semesterRepository.findById(cr.getSemester().getId()).orElse(null);
            if (semester != null) {
                response.setSemesterId(semester.getId());
                response.setSemesterName(semester.getName());
            }
        }

        if (cr.getTimeSlot() != null) {
            TimeSlot timeSlot = timeSlotRepository.findById(cr.getTimeSlot().getId()).orElse(null);
            if (timeSlot != null) {
                response.setTimeSlotId(timeSlot.getId());
                response.setTimeSlotName(timeSlot.getName());
            }
        }

        if (cr.getClassroom() != null) {
            Classroom classroom = classroomRepository.findById(cr.getClassroom().getId()).orElse(null);
            if (classroom != null) {
                response.setClassroomId(classroom.getId());
                response.setClassroomNumber(classroom.getRoomNumber());
                if (classroom.getBuilding() != null) {
                    Building building = classroom.getBuilding();
                    response.setBuildingName(building.getName());
                }
            }
        }

        return response;
    }

    private Subject createSubjectProxy(Long id) {
        Subject s = new Subject();
        s.setId(id);
        return s;
    }

    private Administration createAdministrationProxy(Long id) {
        Administration a = new Administration();
        a.setId(id);
        return a;
    }

    private Section createSectionProxy(Long id) {
        Section s = new Section();
        s.setId(id);
        return s;
    }

    private Semester createSemesterProxy(Long id) {
        Semester s = new Semester();
        s.setId(id);
        return s;
    }

    private TimeSlot createTimeSlotProxy(Long id) {
        TimeSlot t = new TimeSlot();
        t.setId(id);
        return t;
    }

    private Classroom createClassroomProxy(Long id) {
        Classroom c = new Classroom();
        c.setId(id);
        return c;
    }
}