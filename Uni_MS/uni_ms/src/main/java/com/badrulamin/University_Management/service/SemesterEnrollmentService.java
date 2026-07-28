package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.request.EnrollmentApprovalRequest;
import com.badrulamin.University_Management.payload.request.SemesterEnrollmentRequest;
import com.badrulamin.University_Management.payload.response.*;
import com.badrulamin.University_Management.payload.response.EnrollmentDashboardResponse.EnrollmentStatsByDepartment;
import com.badrulamin.University_Management.payload.response.EnrollmentDashboardResponse.EnrollmentStatsByStatus;
import com.badrulamin.University_Management.payload.response.EnrollmentDashboardResponse.RecentEnrollment;
import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SemesterEnrollmentService {

    private final SemesterEnrollmentRepository semesterEnrollmentRepository;
    private final EnrollmentConfigRepository enrollmentConfigRepository;
    private final EnrollmentApprovalRepository enrollmentApprovalRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;
    private final BatchRepository batchRepository;
    private final ProgramRepository programRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    private final EnrollmentHistoryService enrollmentHistoryService;

    public EnrollmentEligibilityResponse checkEligibility(Long studentId, Long semesterId) {
        EnrollmentEligibilityResponse response = new EnrollmentEligibilityResponse();
        response.setStudentId(studentId);
        response.setSemesterId(semesterId);
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", semesterId));

        response.setStudentName(student.getFirstName() + " " + student.getLastName());
        response.setSemesterName(semester.getName());

        boolean alreadyEnrolled = semesterEnrollmentRepository.existsByStudent_IdAndSemester_IdAndDeletedFalse(studentId, semesterId);
        response.setHasActiveEnrollment(alreadyEnrolled);
        if (alreadyEnrolled) {
            errors.add("Student is already enrolled for this semester");
        }

        boolean hasAcademicHold = !"ACTIVE".equals(student.getStatus());
        response.setHasAcademicHold(hasAcademicHold);
        if (hasAcademicHold) {
            errors.add("Student has an academic hold (status: " + student.getStatus() + ")");
        }

        List<CourseRegistration> registrations = courseRegistrationRepository.findByStudent_IdAndSemester_IdAndStatusIn(
                studentId, semesterId, List.of("SELECTED", "APPROVED", "REGISTERED", "PENDING"));
        response.setRegistrationCompleted(!registrations.isEmpty());
        if (registrations.isEmpty()) {
            warnings.add("No course registrations found for this semester");
        }

        List<Invoice> invoices = invoiceRepository.findByStudent_Id(studentId);
        List<Invoice> semesterInvoices = invoices.stream()
                .filter(i -> i.getSemester() != null && i.getSemester().getId().equals(semesterId))
                .collect(Collectors.toList());

        boolean allPaid = semesterInvoices.isEmpty() || semesterInvoices.stream().allMatch(i -> "PAID".equals(i.getStatus()));
        response.setFeesPaid(allPaid);
        if (!allPaid) {
            errors.add("Outstanding fee payments for this semester");
        }

        double outstandingBalance = semesterInvoices.stream()
                .filter(i -> !"PAID".equals(i.getStatus()))
                .mapToDouble(Invoice::getDueAmount)
                .sum();
        response.setCurrentOutstandingBalance((int) outstandingBalance);
        if (outstandingBalance > 0) {
            errors.add("Outstanding balance: " + outstandingBalance);
        }

        response.setEligible(errors.isEmpty());
        response.setErrors(errors);
        response.setWarnings(warnings);

        return response;
    }

    @Transactional
    public SemesterEnrollmentResponse enroll(SemesterEnrollmentRequest request, Long userId) {
        EnrollmentConfig config = enrollmentConfigRepository.findActiveConfig(request.getSemesterId(), LocalDate.now())
                .orElseThrow(() -> new BusinessException("Enrollment is not open for this semester"));

        EnrollmentEligibilityResponse eligibility = checkEligibility(request.getStudentId(), request.getSemesterId());
        if (!eligibility.isEligible()) {
            throw new BusinessException("Student is not eligible for enrollment: " + String.join(", ", eligibility.getErrors()));
        }

        if (semesterEnrollmentRepository.existsByStudent_IdAndSemester_IdAndDeletedFalse(request.getStudentId(), request.getSemesterId())) {
            throw new BusinessException("Student is already enrolled for this semester");
        }

        boolean isLate = config.getLateEnrollmentDate() != null && LocalDate.now().isAfter(config.getEndDate());
        if (isLate && !config.getAllowLateEnrollment()) {
            throw new BusinessException("Late enrollment is not allowed for this semester");
        }

        Integer registeredCredits = request.getRegisteredCredits();
        if (registeredCredits == null) {
            registeredCredits = courseRegistrationRepository.sumCreditHoursByStudentAndSemester(request.getStudentId(), request.getSemesterId());
        }
        if (registeredCredits < config.getMinCredits()) {
            throw new BusinessException("Registered credits (" + registeredCredits + ") below minimum (" + config.getMinCredits() + ")");
        }
        if (registeredCredits > config.getMaxCredits()) {
            throw new BusinessException("Registered credits (" + registeredCredits + ") exceed maximum (" + config.getMaxCredits() + ")");
        }

        String enrollmentNumber = generateEnrollmentNumber(request.getSemesterId());

        boolean requiresAdvisor = config.getRequiresAdvisorApproval();
        String status = requiresAdvisor ? "PENDING" : "APPROVED";
        String advisorStatus = requiresAdvisor ? "PENDING" : "APPROVED";

        SemesterEnrollment enrollment = SemesterEnrollment.builder()
                .student(createStudentProxy(request.getStudentId()))
                .semester(createSemesterProxy(request.getSemesterId()))
                .batch(request.getBatchId() != null ? createBatchProxy(request.getBatchId()) : null)
                .program(request.getProgramId() != null ? createProgramProxy(request.getProgramId()) : null)
                .faculty(request.getFacultyId() != null ? createFacultyProxy(request.getFacultyId()) : null)
                .department(request.getDepartmentId() != null ? createDepartmentProxy(request.getDepartmentId()) : null)
                .advisor(request.getAdvisorId() != null ? createTeacherProxy(request.getAdvisorId()) : null)
                .enrollmentNumber(enrollmentNumber)
                .enrollmentDate(LocalDate.now())
                .status(status)
                .registeredCredits(registeredCredits)
                .minCredits(config.getMinCredits())
                .maxCredits(config.getMaxCredits())
                .advisorStatus(advisorStatus)
                .paymentStatus("PENDING")
                .isFinalized(false)
                .isActive(true)
                .isLateEnrollment(isLate)
                .enrollmentType(isLate ? "LATE" : "NORMAL")
                .remarks(request.getRemarks())
                .build();

        SemesterEnrollment saved = semesterEnrollmentRepository.save(enrollment);

        enrollmentHistoryService.recordHistory(request.getStudentId(), request.getSemesterId(),
                saved.getId(), "ENROLLED", "Student enrolled with enrollment number: " + enrollmentNumber,
                userId, null);

        return toResponse(saved);
    }

    public SemesterEnrollmentResponse getEnrollmentById(Long id) {
        SemesterEnrollment enrollment = semesterEnrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SemesterEnrollment", "id", id));
        return toResponse(enrollment);
    }

    public List<SemesterEnrollmentResponse> getStudentEnrollments(Long studentId) {
        return semesterEnrollmentRepository.findByStudent_IdAndDeletedFalse(studentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SemesterEnrollmentResponse getStudentEnrollmentForSemester(Long studentId, Long semesterId) {
        SemesterEnrollment enrollment = semesterEnrollmentRepository
                .findByStudent_IdAndSemester_IdAndDeletedFalse(studentId, semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("SemesterEnrollment", "studentId/semesterId",
                        studentId + "/" + semesterId));
        return toResponse(enrollment);
    }

    public List<SemesterEnrollmentResponse> getPendingApprovals(Long semesterId) {
        return semesterEnrollmentRepository.findPendingAdvisorApprovals(semesterId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<SemesterEnrollmentResponse> getPendingApprovalsForAdvisor(Long advisorId, Long semesterId) {
        return semesterEnrollmentRepository.findPendingByAdvisor(advisorId, semesterId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentApprovalResponse processApproval(EnrollmentApprovalRequest request, Long userId, String ipAddress) {
        SemesterEnrollment enrollment = semesterEnrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("SemesterEnrollment", "id", request.getEnrollmentId()));

        if (!"PENDING".equals(enrollment.getStatus())) {
            throw new BusinessException("Enrollment is not in PENDING status");
        }

        String action = request.getAction().toUpperCase();
        if (!"APPROVED".equals(action) && !"REJECTED".equals(action)) {
            throw new BusinessException("Action must be APPROVED or REJECTED");
        }

        EnrollmentApproval approval = EnrollmentApproval.builder()
                .semesterEnrollment(createEnrollmentProxy(enrollment.getId()))
                .advisor(createTeacherProxy(userId))
                .action(action)
                .comments(request.getComments())
                .ipAddress(ipAddress)
                .build();

        EnrollmentApproval savedApproval = enrollmentApprovalRepository.save(approval);

        if ("APPROVED".equals(action)) {
            enrollment.setStatus("APPROVED");
            enrollment.setAdvisorStatus("APPROVED");
            enrollment.setAdvisorApprovedAt(LocalDateTime.now());
        } else {
            enrollment.setStatus("REJECTED");
            enrollment.setAdvisorStatus("REJECTED");
        }
        enrollment.setAdvisorComments(request.getComments());
        semesterEnrollmentRepository.save(enrollment);

        enrollmentHistoryService.recordHistory(enrollment.getStudent().getId(), enrollment.getSemester().getId(),
                enrollment.getId(), "ADVISOR_" + action,
                "Advisor " + action.toLowerCase() + " enrollment. Comments: " + request.getComments(),
                userId, ipAddress);

        Student student = studentRepository.findById(enrollment.getStudent().getId()).orElse(null);
        if (student != null && student.getUser() != null) {
            createNotification(student.getUser().getId(),
                    "Enrollment " + action,
                    "Your enrollment " + enrollmentNumber(enrollment) + " has been " + action.toLowerCase(),
                    "ENROLLMENT_" + action, "SEMESTER_ENROLLMENT", "SemesterEnrollment", enrollment.getId());
        }

        EnrollmentApprovalResponse response = new EnrollmentApprovalResponse();
        response.setId(savedApproval.getId());
        response.setEnrollmentId(enrollment.getId());
        response.setEnrollmentNumber(enrollment.getEnrollmentNumber());
        response.setStudentId(enrollment.getStudent().getId());
        response.setStudentName(enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName());
        response.setStudentCode(enrollment.getStudent().getStudentCode());
        response.setSemesterId(enrollment.getSemester().getId());
        response.setSemesterName(enrollment.getSemester().getName());
        response.setAdvisorId(userId);
        response.setAction(action);
        response.setComments(request.getComments());
        response.setCreatedAt(savedApproval.getCreatedAt());
        return response;
    }

    @Transactional
    public SemesterEnrollmentResponse cancelEnrollment(Long enrollmentId, Long userId, String reason) {
        SemesterEnrollment enrollment = semesterEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("SemesterEnrollment", "id", enrollmentId));

        if (!enrollment.canBeCancelled()) {
            throw new BusinessException("Enrollment cannot be cancelled in current status: " + enrollment.getStatus());
        }

        enrollment.setStatus("CANCELLED");
        enrollment.setCancelledAt(LocalDateTime.now());
        enrollment.setCancellationReason(reason);
        enrollment.setIsActive(false);

        SemesterEnrollment saved = semesterEnrollmentRepository.save(enrollment);

        enrollmentHistoryService.recordHistory(enrollment.getStudent().getId(), enrollment.getSemester().getId(),
                enrollment.getId(), "CANCELLED", "Enrollment cancelled. Reason: " + reason, userId, null);

        Student student = studentRepository.findById(enrollment.getStudent().getId()).orElse(null);
        if (student != null && student.getUser() != null) {
            createNotification(student.getUser().getId(),
                    "Enrollment Cancelled",
                    "Your enrollment " + enrollmentNumber(enrollment) + " has been cancelled. Reason: " + reason,
                    "ENROLLMENT_CANCELLED", "SEMESTER_ENROLLMENT", "SemesterEnrollment", enrollment.getId());
        }

        return toResponse(saved);
    }

    @Transactional
    public SemesterEnrollmentResponse forceEnroll(SemesterEnrollmentRequest request, Long userId) {
        if (semesterEnrollmentRepository.existsByStudent_IdAndSemester_IdAndDeletedFalse(request.getStudentId(), request.getSemesterId())) {
            throw new BusinessException("Student is already enrolled for this semester");
        }

        EnrollmentConfig config = enrollmentConfigRepository.findBySemester_IdAndIsActiveTrue(request.getSemesterId())
                .orElse(null);

        Integer registeredCredits = request.getRegisteredCredits();
        if (registeredCredits == null) {
            registeredCredits = courseRegistrationRepository.sumCreditHoursByStudentAndSemester(request.getStudentId(), request.getSemesterId());
        }

        String enrollmentNumber = generateEnrollmentNumber(request.getSemesterId());

        Integer finalCredits = registeredCredits;
        Integer minC = config != null ? config.getMinCredits() : 0;
        Integer maxC = config != null ? config.getMaxCredits() : 99;

        SemesterEnrollment enrollment = SemesterEnrollment.builder()
                .student(createStudentProxy(request.getStudentId()))
                .semester(createSemesterProxy(request.getSemesterId()))
                .batch(request.getBatchId() != null ? createBatchProxy(request.getBatchId()) : null)
                .program(request.getProgramId() != null ? createProgramProxy(request.getProgramId()) : null)
                .faculty(request.getFacultyId() != null ? createFacultyProxy(request.getFacultyId()) : null)
                .department(request.getDepartmentId() != null ? createDepartmentProxy(request.getDepartmentId()) : null)
                .advisor(request.getAdvisorId() != null ? createTeacherProxy(request.getAdvisorId()) : null)
                .enrollmentNumber(enrollmentNumber)
                .enrollmentDate(LocalDate.now())
                .status("APPROVED")
                .registeredCredits(finalCredits)
                .minCredits(minC)
                .maxCredits(maxC)
                .advisorStatus("APPROVED")
                .paymentStatus("PENDING")
                .isFinalized(false)
                .isActive(true)
                .isLateEnrollment(false)
                .enrollmentType("FORCE")
                .remarks(request.getRemarks())
                .build();

        SemesterEnrollment saved = semesterEnrollmentRepository.save(enrollment);

        enrollmentHistoryService.recordHistory(request.getStudentId(), request.getSemesterId(),
                saved.getId(), "FORCE_ENROLLED", "Admin force enrolled student. Enrollment number: " + enrollmentNumber,
                userId, null);

        Student student = studentRepository.findById(request.getStudentId()).orElse(null);
        if (student != null && student.getUser() != null) {
            createNotification(student.getUser().getId(),
                    "Force Enrollment",
                    "You have been force enrolled for semester. Enrollment number: " + enrollmentNumber,
                    "ENROLLMENT_FORCE", "SEMESTER_ENROLLMENT", "SemesterEnrollment", saved.getId());
        }

        return toResponse(saved);
    }

    @Transactional
    public SemesterEnrollmentResponse reopenEnrollment(Long enrollmentId, Long userId) {
        SemesterEnrollment enrollment = semesterEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("SemesterEnrollment", "id", enrollmentId));

        if (!"CANCELLED".equals(enrollment.getStatus()) && !"REJECTED".equals(enrollment.getStatus())) {
            throw new BusinessException("Only cancelled or rejected enrollments can be reopened");
        }

        enrollment.setStatus("DRAFT");
        enrollment.setAdvisorStatus("PENDING");
        enrollment.setIsActive(true);
        enrollment.setCancelledAt(null);
        enrollment.setCancellationReason(null);

        SemesterEnrollment saved = semesterEnrollmentRepository.save(enrollment);

        enrollmentHistoryService.recordHistory(enrollment.getStudent().getId(), enrollment.getSemester().getId(),
                enrollment.getId(), "REOPENED", "Enrollment reopened", userId, null);

        Student student = studentRepository.findById(enrollment.getStudent().getId()).orElse(null);
        if (student != null && student.getUser() != null) {
            createNotification(student.getUser().getId(),
                    "Enrollment Reopened",
                    "Your enrollment " + enrollmentNumber(enrollment) + " has been reopened for review.",
                    "ENROLLMENT_REOPENED", "SEMESTER_ENROLLMENT", "SemesterEnrollment", enrollment.getId());
        }

        return toResponse(saved);
    }

    @Transactional
    public SemesterEnrollmentResponse finalizeEnrollment(Long enrollmentId, Long userId) {
        SemesterEnrollment enrollment = semesterEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("SemesterEnrollment", "id", enrollmentId));

        if (!"APPROVED".equals(enrollment.getStatus())) {
            throw new BusinessException("Only approved enrollments can be finalized");
        }

        EnrollmentConfig config = enrollmentConfigRepository.findBySemester_IdAndIsActiveTrue(enrollment.getSemester().getId()).orElse(null);
        if (config != null && config.getRequiresPayment()) {
            if (!"PAID".equals(enrollment.getPaymentStatus())) {
                throw new BusinessException("Payment is required but not completed");
            }
        }

        enrollment.setIsFinalized(true);
        enrollment.setFinalizedAt(LocalDateTime.now());
        enrollment.setStatus("COMPLETED");

        SemesterEnrollment saved = semesterEnrollmentRepository.save(enrollment);

        enrollmentHistoryService.recordHistory(enrollment.getStudent().getId(), enrollment.getSemester().getId(),
                enrollment.getId(), "FINALIZED", "Enrollment finalized and completed", userId, null);

        return toResponse(saved);
    }

    public EnrollmentDashboardResponse getDashboardStats(Long semesterId) {
        EnrollmentDashboardResponse response = new EnrollmentDashboardResponse();

        response.setTotalEnrollments(semesterEnrollmentRepository.countBySemester_IdAndDeletedFalse(semesterId));
        response.setPendingApprovals(semesterEnrollmentRepository.countBySemester_IdAndStatusAndDeletedFalse(semesterId, "PENDING"));
        response.setApprovedEnrollments(semesterEnrollmentRepository.countBySemester_IdAndStatusAndDeletedFalse(semesterId, "APPROVED"));
        response.setCompletedEnrollments(semesterEnrollmentRepository.countBySemester_IdAndStatusAndDeletedFalse(semesterId, "COMPLETED"));
        response.setRejectedEnrollments(semesterEnrollmentRepository.countBySemester_IdAndStatusAndDeletedFalse(semesterId, "REJECTED"));
        response.setCancelledEnrollments(semesterEnrollmentRepository.countBySemester_IdAndStatusAndDeletedFalse(semesterId, "CANCELLED"));
        response.setDraftEnrollments(semesterEnrollmentRepository.countBySemester_IdAndStatusAndDeletedFalse(semesterId, "DRAFT"));

        List<EnrollmentStatsByStatus> statusBreakdown = new ArrayList<>();
        for (String status : List.of("DRAFT", "PENDING", "APPROVED", "REJECTED", "CANCELLED", "COMPLETED")) {
            long count = semesterEnrollmentRepository.countBySemester_IdAndStatusAndDeletedFalse(semesterId, status);
            EnrollmentStatsByStatus stats = new EnrollmentStatsByStatus();
            stats.setStatus(status);
            stats.setCount(count);
            statusBreakdown.add(stats);
        }
        response.setStatusBreakdown(statusBreakdown);

        List<SemesterEnrollment> allEnrollments = semesterEnrollmentRepository.findBySemester_IdAndDeletedFalse(semesterId);
        Map<Long, List<SemesterEnrollment>> byDepartment = allEnrollments.stream()
                .filter(e -> e.getDepartment() != null)
                .collect(Collectors.groupingBy(e -> e.getDepartment().getId()));

        List<EnrollmentStatsByDepartment> deptBreakdown = new ArrayList<>();
        byDepartment.forEach((deptId, enrollments) -> {
            EnrollmentStatsByDepartment deptStats = new EnrollmentStatsByDepartment();
            deptStats.setDepartmentId(deptId);
            Department dept = departmentRepository.findById(deptId).orElse(null);
            deptStats.setDepartmentName(dept != null ? dept.getName() : "Unknown");
            deptStats.setCount((long) enrollments.size());
            deptBreakdown.add(deptStats);
        });
        response.setDepartmentBreakdown(deptBreakdown);

        List<RecentEnrollment> recentEnrollments = new ArrayList<>();
        List<SemesterEnrollment> recent = allEnrollments.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .collect(Collectors.toList());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (SemesterEnrollment e : recent) {
            RecentEnrollment recentEnrollment = new RecentEnrollment();
            recentEnrollment.setId(e.getId());
            recentEnrollment.setEnrollmentNumber(e.getEnrollmentNumber());
            Student student = e.getStudent();
            recentEnrollment.setStudentName(student.getFirstName() + " " + student.getLastName());
            recentEnrollment.setStudentCode(student.getStudentCode());
            recentEnrollment.setSemesterName(e.getSemester().getName());
            recentEnrollment.setStatus(e.getStatus());
            recentEnrollment.setRegisteredCredits(e.getRegisteredCredits());
            recentEnrollment.setAdvisorStatus(e.getAdvisorStatus());
            recentEnrollment.setPaymentStatus(e.getPaymentStatus());
            recentEnrollment.setCreatedAt(e.getCreatedAt() != null ? e.getCreatedAt().format(formatter) : null);
            recentEnrollments.add(recentEnrollment);
        }
        response.setRecentEnrollments(recentEnrollments);

        return response;
    }

    public Page<SemesterEnrollmentResponse> getFilteredEnrollments(Long semesterId, Long departmentId,
                                                                   Long facultyId, Long programId,
                                                                   String status, Pageable pageable) {
        return semesterEnrollmentRepository.findFiltered(semesterId, departmentId, facultyId, programId, status, pageable)
                .map(this::toResponse);
    }

    public Page<SemesterEnrollmentResponse> getAllEnrollments(Pageable pageable) {
        return semesterEnrollmentRepository.findByDeletedFalse(pageable)
                .map(this::toResponse);
    }

    private SemesterEnrollmentResponse toResponse(SemesterEnrollment e) {
        SemesterEnrollmentResponse response = new SemesterEnrollmentResponse();
        response.setId(e.getId());
        response.setEnrollmentNumber(e.getEnrollmentNumber());
        response.setStudentId(e.getStudent().getId());
        response.setStudentName(e.getStudent().getFirstName() + " " + e.getStudent().getLastName());
        response.setStudentCode(e.getStudent().getStudentCode());
        response.setStudentEmail(e.getStudent().getEmail());
        response.setSemesterId(e.getSemester().getId());
        response.setSemesterName(e.getSemester().getName());
        response.setBatchId(e.getBatch() != null ? e.getBatch().getId() : null);
        response.setBatchName(e.getBatch() != null ? e.getBatch().getName() : null);
        response.setProgramId(e.getProgram() != null ? e.getProgram().getId() : null);
        response.setProgramName(e.getProgram() != null ? e.getProgram().getName() : null);
        response.setFacultyId(e.getFaculty() != null ? e.getFaculty().getId() : null);
        response.setFacultyName(e.getFaculty() != null ? e.getFaculty().getName() : null);
        response.setDepartmentId(e.getDepartment() != null ? e.getDepartment().getId() : null);
        response.setDepartmentName(e.getDepartment() != null ? e.getDepartment().getName() : null);
        response.setAdvisorId(e.getAdvisor() != null ? e.getAdvisor().getId() : null);
        response.setAdvisorName(e.getAdvisor() != null ? e.getAdvisor().getFirstName() + " " + e.getAdvisor().getLastName() : null);
        response.setEnrollmentDate(e.getEnrollmentDate());
        response.setStatus(e.getStatus());
        response.setRegisteredCredits(e.getRegisteredCredits());
        response.setMinCredits(e.getMinCredits());
        response.setMaxCredits(e.getMaxCredits());
        response.setAdvisorStatus(e.getAdvisorStatus());
        response.setAdvisorComments(e.getAdvisorComments());
        response.setAdvisorApprovedAt(e.getAdvisorApprovedAt());
        response.setPaymentStatus(e.getPaymentStatus());
        response.setPaymentAmount(e.getPaymentAmount());
        response.setPaymentReference(e.getPaymentReference());
        response.setPaymentDate(e.getPaymentDate());
        response.setIsFinalized(e.getIsFinalized());
        response.setFinalizedAt(e.getFinalizedAt());
        response.setRemarks(e.getRemarks());
        response.setIsActive(e.getIsActive());
        response.setIsLateEnrollment(e.getIsLateEnrollment());
        response.setEnrollmentType(e.getEnrollmentType());
        response.setCancelledAt(e.getCancelledAt());
        response.setCancellationReason(e.getCancellationReason());
        response.setCreatedAt(e.getCreatedAt());
        response.setUpdatedAt(e.getUpdatedAt());
        return response;
    }

    private String enrollmentNumber(SemesterEnrollment e) {
        return e.getEnrollmentNumber() != null ? e.getEnrollmentNumber() : "N/A";
    }

    private String generateEnrollmentNumber(Long semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", semesterId));
        long count = semesterEnrollmentRepository.countBySemester_IdAndDeletedFalse(semesterId);
        String year = String.valueOf(semester.getStartDate().getYear());
        String seq = String.format("%05d", count + 1);
        return "SE" + year + "-" + seq;
    }

    private void createNotification(Long userId, String title, String message, String type,
                                    String module, String referenceType, Long referenceId) {
        try {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(type);
            notification.setModule(module);
            notification.setReferenceType(referenceType);
            notification.setReferenceId(referenceId);
            notificationRepository.save(notification);
        } catch (Exception ignored) {
        }
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

    private Batch createBatchProxy(Long id) {
        Batch b = new Batch();
        b.setId(id);
        return b;
    }

    private Program createProgramProxy(Long id) {
        Program p = new Program();
        p.setId(id);
        return p;
    }

    private Faculty createFacultyProxy(Long id) {
        Faculty f = new Faculty();
        f.setId(id);
        return f;
    }

    private Teacher createTeacherProxy(Long id) {
        Teacher t = new Teacher();
        t.setId(id);
        return t;
    }

    private Department createDepartmentProxy(Long id) {
        Department d = new Department();
        d.setId(id);
        return d;
    }

    private SemesterEnrollment createEnrollmentProxy(Long id) {
        SemesterEnrollment e = new SemesterEnrollment();
        e.setId(id);
        return e;
    }
}