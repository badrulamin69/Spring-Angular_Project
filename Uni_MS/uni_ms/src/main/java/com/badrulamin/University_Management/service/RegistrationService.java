package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.request.*;
import com.badrulamin.University_Management.payload.response.*;
import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationService {

    private final CourseRegistrationRepository courseRegistrationRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final BatchRepository batchRepository;
    private final SubjectOfferingRepository subjectOfferingRepository;
    private final RegistrationConfigService registrationConfigService;
    private final ValidationService validationService;
    private final EligibilityService eligibilityService;
    private final RegistrationHistoryService historyService;

    public List<CourseRegistration> getStudentRegistrations(Long studentId, Long semesterId) {
        List<String> statuses = List.of("SELECTED", "PENDING", "APPROVED", "REGISTERED", "DROPPED");
        return courseRegistrationRepository.findByStudent_IdAndSemester_IdAndStatusIn(studentId, semesterId, statuses);
    }

    public RegistrationSummaryResponse getRegistrationSummary(Long studentId, Long semesterId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", semesterId));

        RegistrationConfig config = registrationConfigService.getActiveConfigOrThrow(semesterId);
        Integer totalCredits = courseRegistrationRepository.sumCreditHoursByStudentAndSemester(studentId, semesterId);
        List<CourseRegistration> registrations = getStudentRegistrations(studentId, semesterId);

        RegistrationSummaryResponse summary = new RegistrationSummaryResponse();
        summary.setStudentId(studentId);
        summary.setStudentName(student.getFirstName() + " " + student.getLastName());
        summary.setStudentCode(student.getStudentCode());
        summary.setSemesterId(semesterId);
        summary.setSemesterName(semester.getName());
        summary.setTotalCreditsRegistered(totalCredits);
        summary.setMinCreditsRequired(config.getMinCredits());
        summary.setMaxCreditsAllowed(config.getMaxCredits());
        summary.setErrors(new ArrayList<>());

        List<RegistrationSummaryResponse.RegisteredCourseItem> items = registrations.stream()
                .map(reg -> {
                    RegistrationSummaryResponse.RegisteredCourseItem item = new RegistrationSummaryResponse.RegisteredCourseItem();
                    item.setRegistrationId(reg.getId());
                    item.setSubjectId(reg.getCourse() != null ? reg.getCourse().getId() : null);
                    item.setSubjectName(reg.getCourse() != null ? reg.getCourse().getName() : null);
                    item.setSubjectCode(reg.getCourse() != null ? reg.getCourse().getCode() : null);
                    item.setCreditHours(reg.getCreditHours());
                    item.setStatus(reg.getStatus());
                    item.setAdvisorStatus(reg.getAdvisorStatus());
                    item.setPaymentStatus(reg.getPaymentStatus());
                    return item;
                })
                .collect(Collectors.toList());

        summary.setRegisteredCourses(items);
        summary.setLastUpdated(LocalDateTime.now());

        return summary;
    }

    @Transactional
    public CourseRegistration selectCourse(CourseRegistrationRequest request, Long userId) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        Course course = subjectRepository.findById(request.getSubjectId())
                .map(s -> {
                    Course c = new Course();
                    c.setId(s.getId());
                    return c;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getSubjectId()));

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));

        registrationConfigService.getActiveConfigOrThrow(request.getSemesterId());

        Integer currentTotal = courseRegistrationRepository.sumCreditHoursByStudentAndSemester(
                request.getStudentId(), request.getSemesterId());

        List<String> errors = validationService.validateRegistration(
                request.getStudentId(), request.getSubjectId(), request.getSemesterId(),
                request.getBatchId(), null);

        if (!errors.isEmpty()) {
            throw new BusinessException(String.join("; ", errors));
        }

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));

        CourseRegistration registration = CourseRegistration.builder()
                .student(student)
                .course(course)
                .semester(semester)
                .batch(request.getBatchId() != null ? createBatchProxy(request.getBatchId()) : null)
                .status("SELECTED")
                .registrationDate(LocalDate.now())
                .isSelected(true)
                .creditHours(subject.getCredits())
                .registrationType(request.getRegistrationType() != null ? request.getRegistrationType() : "NORMAL")
                .advisorStatus("PENDING")
                .paymentStatus("PENDING")
                .remarks(request.getRemarks())
                .build();

        CourseRegistration saved = courseRegistrationRepository.save(registration);

        historyService.recordHistory(
                request.getStudentId(), request.getSubjectId(), request.getSemesterId(),
                saved.getId(), "COURSE_SELECTED",
                "Student selected course: " + subject.getName() + " (" + subject.getCode() + ")",
                userId, null
        );

        return saved;
    }

    @Transactional
    public void dropCourse(Long registrationId, Long userId) {
        CourseRegistration registration = courseRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseRegistration", "id", registrationId));

        RegistrationConfig config = registrationConfigService.getActiveConfigOrThrow(
                registration.getSemester().getId());

        if (!config.getAllowAddDrop()) {
            throw new BusinessException("Add/Drop is not allowed for this registration period");
        }

        if (config.getAddDropDeadline() != null && LocalDate.now().isAfter(config.getAddDropDeadline())) {
            throw new BusinessException("Add/Drop deadline has passed");
        }

        registration.setStatus("DROPPED");
        registration.setIsSelected(false);
        courseRegistrationRepository.save(registration);

        historyService.recordHistory(
                registration.getStudent().getId(),
                registration.getCourse() != null ? registration.getCourse().getId() : null,
                registration.getSemester().getId(),
                registrationId, "COURSE_DROPPED",
                "Student dropped course",
                userId, null
        );
    }

    @Transactional
    public CourseRegistration finalizeRegistration(Long registrationId, Long userId) {
        CourseRegistration registration = courseRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseRegistration", "id", registrationId));

        if (!"APPROVED".equals(registration.getAdvisorStatus())) {
            throw new BusinessException("Registration must be approved by advisor before finalization");
        }

        RegistrationConfig config = registrationConfigService.getActiveConfigOrThrow(
                registration.getSemester().getId());

        if (config.getPaymentRequired() && !"PAID".equals(registration.getPaymentStatus())) {
            throw new BusinessException("Payment must be completed before finalization");
        }

        registration.setStatus("REGISTERED");
        registration.setFinalized(true);
        registration.setFinalizedAt(LocalDateTime.now());

        CourseRegistration saved = courseRegistrationRepository.save(registration);

        historyService.recordHistory(
                registration.getStudent().getId(),
                registration.getCourse() != null ? registration.getCourse().getId() : null,
                registration.getSemester().getId(),
                registrationId, "REGISTRATION_FINALIZED",
                "Registration finalized and confirmed",
                userId, null
        );

        return saved;
    }

    @Transactional
    public AdvisorApprovalResponse processPaymentValidation(PaymentValidationRequest request, Long userId) {
        CourseRegistration registration = courseRegistrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new ResourceNotFoundException("CourseRegistration", "id", request.getRegistrationId()));

        if (!"APPROVED".equals(registration.getAdvisorStatus())) {
            throw new BusinessException("Registration must be approved by advisor before payment");
        }

        registration.setPaymentStatus("PAID");
        registration.setPaymentReference(request.getPaymentReference());
        registration.setPaymentAmount(request.getPaymentAmount());
        courseRegistrationRepository.save(registration);

        historyService.recordHistory(
                registration.getStudent().getId(),
                registration.getCourse() != null ? registration.getCourse().getId() : null,
                registration.getSemester().getId(),
                request.getRegistrationId(), "PAYMENT_RECEIVED",
                "Payment received. Reference: " + request.getPaymentReference() + ", Amount: " + request.getPaymentAmount(),
                userId, null
        );

        AdvisorApprovalResponse response = new AdvisorApprovalResponse();
        response.setStudentId(registration.getStudent().getId());
        response.setProcessedRegistrationIds(List.of(request.getRegistrationId()));
        response.setProcessedAt(LocalDateTime.now());
        return response;
    }

    public RegistrationDashboardResponse getDashboardStats(Long semesterId) {
        RegistrationDashboardResponse dashboard = new RegistrationDashboardResponse();

        dashboard.setTotalRegistrations(courseRegistrationRepository.countBySemester_Id(semesterId));
        dashboard.setPendingApprovals(courseRegistrationRepository.countBySemesterIdAndAdvisorStatus(semesterId, "PENDING"));
        dashboard.setApprovedRegistrations(courseRegistrationRepository.countBySemesterIdAndAdvisorStatus(semesterId, "APPROVED"));
        dashboard.setRegisteredStudents(courseRegistrationRepository.countBySemesterIdAndStatus(semesterId, "REGISTERED"));
        dashboard.setDroppedRegistrations(courseRegistrationRepository.countBySemesterIdAndStatus(semesterId, "DROPPED"));

        List<Object[]> statusGroups = courseRegistrationRepository.countGroupByStatus(semesterId);
        List<RegistrationDashboardResponse.RegistrationStatsByStatus> breakdown = statusGroups.stream()
                .map(row -> {
                    RegistrationDashboardResponse.RegistrationStatsByStatus stat = new RegistrationDashboardResponse.RegistrationStatsByStatus();
                    stat.setStatus((String) row[0]);
                    stat.setCount((Long) row[1]);
                    return stat;
                })
                .collect(Collectors.toList());
        dashboard.setStatusBreakdown(breakdown);

        List<CourseRegistration> recentRecords = courseRegistrationRepository
                .findBySemester_IdOrderByCreatedAtDesc(semesterId, PageRequest.of(0, 10));
        List<RegistrationDashboardResponse.RecentRegistration> recent = recentRecords.stream()
                .map(reg -> {
                    RegistrationDashboardResponse.RecentRegistration r = new RegistrationDashboardResponse.RecentRegistration();
                    r.setId(reg.getId());
                    r.setStudentName(reg.getStudent() != null ? reg.getStudent().getFirstName() + " " + reg.getStudent().getLastName() : null);
                    r.setStudentCode(reg.getStudent() != null ? reg.getStudent().getStudentCode() : null);
                    r.setCourseName(reg.getCourse() != null ? reg.getCourse().getName() : null);
                    r.setSemesterName(reg.getSemester() != null ? reg.getSemester().getName() : null);
                    r.setStatus(reg.getStatus());
                    r.setCreditHours(reg.getCreditHours());
                    r.setRegistrationDate(reg.getCreatedAt());
                    return r;
                })
                .collect(Collectors.toList());
        dashboard.setRecentRegistrations(recent);

        return dashboard;
    }

    public Page<CourseRegistration> getAllRegistrations(Pageable pageable, String status, Long semesterId) {
        return courseRegistrationRepository.findAll(pageable);
    }

    public CourseRegistration getRegistrationById(Long id) {
        return courseRegistrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseRegistration", "id", id));
    }

    private Batch createBatchProxy(Long id) {
        Batch b = new Batch();
        b.setId(id);
        return b;
    }
}