package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.EnrollmentHistory;
import com.badrulamin.University_Management.entity.SemesterEnrollment;
import com.badrulamin.University_Management.entity.Student;
import com.badrulamin.University_Management.entity.Semester;
import com.badrulamin.University_Management.repository.EnrollmentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentHistoryService {

    private final EnrollmentHistoryRepository enrollmentHistoryRepository;

    @Transactional
    public EnrollmentHistory recordHistory(Long studentId, Long semesterId, Long enrollmentId,
                                           String action, String details, Long performedById, String ipAddress) {
        EnrollmentHistory history = EnrollmentHistory.builder()
                .student(createStudentProxy(studentId))
                .semester(createSemesterProxy(semesterId))
                .semesterEnrollment(enrollmentId != null ? createEnrollmentProxy(enrollmentId) : null)
                .action(action)
                .details(details)
                .ipAddress(ipAddress)
                .build();

        if (performedById != null) {
            history.setPerformedById(performedById);
        }

        return enrollmentHistoryRepository.save(history);
    }

    public Page<EnrollmentHistory> getHistoryByStudent(Long studentId, Pageable pageable) {
        return enrollmentHistoryRepository.findByStudent_IdOrderByCreatedAtDesc(studentId, pageable);
    }

    public List<EnrollmentHistory> getHistoryBySemester(Long semesterId) {
        return enrollmentHistoryRepository.findBySemester_IdOrderByCreatedAtDesc(semesterId);
    }

    public List<EnrollmentHistory> getHistoryByEnrollment(Long enrollmentId) {
        return enrollmentHistoryRepository.findBySemesterEnrollment_IdOrderByCreatedAtDesc(enrollmentId);
    }

    private Student createStudentProxy(Long id) {
        Student s = new Student();
        s.setId(id);
        return s;
    }

    private Semester createSemesterProxy(Long id) {
        Semester s = new Semester();
        s.setId(id);
        return s;
    }

    private SemesterEnrollment createEnrollmentProxy(Long id) {
        SemesterEnrollment e = new SemesterEnrollment();
        e.setId(id);
        return e;
    }
}
