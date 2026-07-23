package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AcademicSession;
import com.badrulamin.University_Management.entity.EnrollmentConfig;
import com.badrulamin.University_Management.entity.Semester;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.request.EnrollmentConfigRequest;
import com.badrulamin.University_Management.payload.response.EnrollmentConfigResponse;
import com.badrulamin.University_Management.repository.EnrollmentConfigRepository;
import com.badrulamin.University_Management.repository.SemesterEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentConfigService {

    private final EnrollmentConfigRepository enrollmentConfigRepository;
    private final SemesterEnrollmentRepository semesterEnrollmentRepository;

    public List<EnrollmentConfigResponse> findAll() {
        return enrollmentConfigRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<EnrollmentConfigResponse> findActive() {
        return enrollmentConfigRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EnrollmentConfigResponse findById(Long id) {
        EnrollmentConfig config = enrollmentConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EnrollmentConfig", "id", id));
        return toResponse(config);
    }

    public EnrollmentConfigResponse findBySemester(Long semesterId) {
        EnrollmentConfig config = enrollmentConfigRepository.findBySemester_IdAndIsActiveTrue(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("EnrollmentConfig", "semesterId", semesterId));
        return toResponse(config);
    }

    public EnrollmentConfig getActiveConfigOrThrow(Long semesterId) {
        return enrollmentConfigRepository.findActiveConfig(semesterId, LocalDate.now())
                .orElseThrow(() -> new BusinessException("Enrollment is not open for this semester"));
    }

    public boolean isEnrollmentOpen(Long semesterId) {
        return enrollmentConfigRepository.findActiveConfig(semesterId, LocalDate.now()).isPresent();
    }

    @Transactional
    public EnrollmentConfigResponse create(EnrollmentConfigRequest request) {
        if (enrollmentConfigRepository.existsBySemester_IdAndIsActiveTrue(request.getSemesterId())) {
            throw new BusinessException("An active enrollment config already exists for this semester");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        EnrollmentConfig config = EnrollmentConfig.builder()
                .semester(createSemesterProxy(request.getSemesterId()))
                .academicSession(request.getAcademicSessionId() != null ? createAcademicSessionProxy(request.getAcademicSessionId()) : null)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .lateEnrollmentDate(request.getLateEnrollmentDate())
                .minCredits(request.getMinCredits())
                .maxCredits(request.getMaxCredits())
                .enrollmentStatus(request.getEnrollmentStatus() != null ? request.getEnrollmentStatus() : "OPEN")
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .isClosed(false)
                .requiresAdvisorApproval(request.getRequiresAdvisorApproval() != null ? request.getRequiresAdvisorApproval() : true)
                .requiresPayment(request.getRequiresPayment() != null ? request.getRequiresPayment() : true)
                .allowLateEnrollment(request.getAllowLateEnrollment() != null ? request.getAllowLateEnrollment() : true)
                .remarks(request.getRemarks())
                .build();

        return toResponse(enrollmentConfigRepository.save(config));
    }

    @Transactional
    public EnrollmentConfigResponse update(Long id, EnrollmentConfigRequest request) {
        EnrollmentConfig config = enrollmentConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EnrollmentConfig", "id", id));

        if (config.getIsClosed()) {
            throw new BusinessException("Cannot update a closed enrollment configuration");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        config.setAcademicSession(request.getAcademicSessionId() != null ? createAcademicSessionProxy(request.getAcademicSessionId()) : config.getAcademicSession());
        config.setStartDate(request.getStartDate());
        config.setEndDate(request.getEndDate());
        config.setLateEnrollmentDate(request.getLateEnrollmentDate());
        config.setMinCredits(request.getMinCredits());
        config.setMaxCredits(request.getMaxCredits());
        config.setEnrollmentStatus(request.getEnrollmentStatus());
        config.setIsActive(request.getIsActive());
        config.setRequiresAdvisorApproval(request.getRequiresAdvisorApproval());
        config.setRequiresPayment(request.getRequiresPayment());
        config.setAllowLateEnrollment(request.getAllowLateEnrollment());
        config.setRemarks(request.getRemarks());

        return toResponse(enrollmentConfigRepository.save(config));
    }

    @Transactional
    public EnrollmentConfigResponse closeEnrollment(Long id) {
        EnrollmentConfig config = enrollmentConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EnrollmentConfig", "id", id));

        config.setIsClosed(true);
        config.setEnrollmentStatus("CLOSED");

        return toResponse(enrollmentConfigRepository.save(config));
    }

    @Transactional
    public EnrollmentConfigResponse reopenEnrollment(Long id) {
        EnrollmentConfig config = enrollmentConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EnrollmentConfig", "id", id));

        config.setIsClosed(false);
        config.setEnrollmentStatus("OPEN");

        return toResponse(enrollmentConfigRepository.save(config));
    }

    @Transactional
    public void delete(Long id) {
        EnrollmentConfig config = enrollmentConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EnrollmentConfig", "id", id));

        long enrollmentCount = semesterEnrollmentRepository.countBySemester_IdAndDeletedFalse(config.getSemester().getId());
        if (enrollmentCount > 0) {
            throw new BusinessException("Cannot delete enrollment config with existing enrollments");
        }

        enrollmentConfigRepository.delete(config);
    }

    private EnrollmentConfigResponse toResponse(EnrollmentConfig config) {
        EnrollmentConfigResponse response = new EnrollmentConfigResponse();
        response.setId(config.getId());
        response.setSemesterId(config.getSemester() != null ? config.getSemester().getId() : null);
        response.setSemesterName(config.getSemester() != null ? config.getSemester().getName() : null);
        response.setAcademicSessionId(config.getAcademicSession() != null ? config.getAcademicSession().getId() : null);
        response.setAcademicSessionName(config.getAcademicSession() != null ? config.getAcademicSession().getName() : null);
        response.setStartDate(config.getStartDate());
        response.setEndDate(config.getEndDate());
        response.setLateEnrollmentDate(config.getLateEnrollmentDate());
        response.setMinCredits(config.getMinCredits());
        response.setMaxCredits(config.getMaxCredits());
        response.setEnrollmentStatus(config.getEnrollmentStatus());
        response.setIsActive(config.getIsActive());
        response.setIsClosed(config.getIsClosed());
        response.setRequiresAdvisorApproval(config.getRequiresAdvisorApproval());
        response.setRequiresPayment(config.getRequiresPayment());
        response.setAllowLateEnrollment(config.getAllowLateEnrollment());
        response.setRemarks(config.getRemarks());
        response.setCreatedAt(config.getCreatedAt());
        return response;
    }

    private Semester createSemesterProxy(Long id) {
        Semester semester = new Semester();
        semester.setId(id);
        return semester;
    }

    private AcademicSession createAcademicSessionProxy(Long id) {
        AcademicSession session = new AcademicSession();
        session.setId(id);
        return session;
    }
}