package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.RegistrationHistory;
import com.badrulamin.University_Management.repository.RegistrationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationHistoryService {

    private final RegistrationHistoryRepository registrationHistoryRepository;

    @Transactional
    public RegistrationHistory recordHistory(Long studentId, Long courseId, Long semesterId,
                                              Long courseRegistrationId, String action,
                                              String details, Long performedById, String ipAddress) {
        RegistrationHistory history = RegistrationHistory.builder()
                .student(createStudentProxy(studentId))
                .course(courseId != null ? createCourseProxy(courseId) : null)
                .semester(createSemesterProxy(semesterId))
                .courseRegistration(courseRegistrationId != null ? createRegistrationProxy(courseRegistrationId) : null)
                .action(action)
                .details(details)
                .ipAddress(ipAddress)
                .build();

        if (performedById != null) {
            history.setPerformedById(performedById);
        }

        return registrationHistoryRepository.save(history);
    }

    public Page<RegistrationHistory> getHistoryByStudent(Long studentId, Pageable pageable) {
        return registrationHistoryRepository.findByStudent_IdOrderByCreatedAtDesc(studentId, pageable);
    }

    public List<RegistrationHistory> getHistoryBySemester(Long semesterId) {
        return registrationHistoryRepository.findBySemester_IdOrderByCreatedAtDesc(semesterId);
    }

    public List<RegistrationHistory> getHistoryByRegistration(Long registrationId) {
        return registrationHistoryRepository.findByCourseRegistration_IdOrderByCreatedAtDesc(registrationId);
    }

    private com.badrulamin.University_Management.entity.Student createStudentProxy(Long id) {
        com.badrulamin.University_Management.entity.Student s = new com.badrulamin.University_Management.entity.Student();
        s.setId(id);
        return s;
    }

    private com.badrulamin.University_Management.entity.Course createCourseProxy(Long id) {
        com.badrulamin.University_Management.entity.Course c = new com.badrulamin.University_Management.entity.Course();
        c.setId(id);
        return c;
    }

    private com.badrulamin.University_Management.entity.Semester createSemesterProxy(Long id) {
        com.badrulamin.University_Management.entity.Semester s = new com.badrulamin.University_Management.entity.Semester();
        s.setId(id);
        return s;
    }

    private com.badrulamin.University_Management.entity.CourseRegistration createRegistrationProxy(Long id) {
        com.badrulamin.University_Management.entity.CourseRegistration r = new com.badrulamin.University_Management.entity.CourseRegistration();
        r.setId(id);
        return r;
    }
}
