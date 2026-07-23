package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.RegistrationConfig;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.request.RegistrationConfigRequest;
import com.badrulamin.University_Management.payload.response.RegistrationConfigResponse;
import com.badrulamin.University_Management.repository.RegistrationConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegistrationConfigService {

    private final RegistrationConfigRepository registrationConfigRepository;

    public List<RegistrationConfigResponse> findAll() {
        return registrationConfigRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<RegistrationConfigResponse> findActive() {
        return registrationConfigRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RegistrationConfigResponse findById(Long id) {
        RegistrationConfig config = registrationConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RegistrationConfig", "id", id));
        return toResponse(config);
    }

    public RegistrationConfigResponse findBySemester(Long semesterId) {
        RegistrationConfig config = registrationConfigRepository.findBySemester_IdAndIsActiveTrue(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("RegistrationConfig", "semesterId", semesterId));
        return toResponse(config);
    }

    public RegistrationConfig getActiveConfigOrThrow(Long semesterId) {
        return registrationConfigRepository.findActiveConfig(semesterId, LocalDate.now())
                .orElseThrow(() -> new BusinessException("Registration is not open for this semester"));
    }

    public boolean isRegistrationOpen(Long semesterId) {
        return registrationConfigRepository.findActiveConfig(semesterId, LocalDate.now()).isPresent();
    }

    @Transactional
    public RegistrationConfigResponse create(RegistrationConfigRequest request) {
        if (registrationConfigRepository.existsBySemester_IdAndIsActiveTrue(request.getSemesterId())) {
            throw new BusinessException("An active registration config already exists for this semester");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        if (request.getAddDropDeadline() != null && request.getAddDropDeadline().isAfter(request.getEndDate())) {
            throw new BusinessException("Add/Drop deadline must be before or on the end date");
        }

        RegistrationConfig config = RegistrationConfig.builder()
                .semester(createSemesterProxy(request.getSemesterId()))
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .minCredits(request.getMinCredits())
                .maxCredits(request.getMaxCredits())
                .allowAddDrop(request.getAllowAddDrop())
                .addDropDeadline(request.getAddDropDeadline())
                .advisorApprovalRequired(request.getAdvisorApprovalRequired())
                .paymentRequired(request.getPaymentRequired())
                .isActive(request.getIsActive())
                .remarks(request.getRemarks())
                .status("ACTIVE")
                .build();

        return toResponse(registrationConfigRepository.save(config));
    }

    @Transactional
    public RegistrationConfigResponse update(Long id, RegistrationConfigRequest request) {
        RegistrationConfig config = registrationConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RegistrationConfig", "id", id));

        if (config.getIsClosed()) {
            throw new BusinessException("Cannot update a closed registration configuration");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        config.setStartDate(request.getStartDate());
        config.setEndDate(request.getEndDate());
        config.setMinCredits(request.getMinCredits());
        config.setMaxCredits(request.getMaxCredits());
        config.setAllowAddDrop(request.getAllowAddDrop());
        config.setAddDropDeadline(request.getAddDropDeadline());
        config.setAdvisorApprovalRequired(request.getAdvisorApprovalRequired());
        config.setPaymentRequired(request.getPaymentRequired());
        config.setIsActive(request.getIsActive());
        config.setRemarks(request.getRemarks());

        return toResponse(registrationConfigRepository.save(config));
    }

    @Transactional
    public RegistrationConfigResponse closeRegistration(Long id) {
        RegistrationConfig config = registrationConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RegistrationConfig", "id", id));

        config.setIsClosed(true);
        config.setStatus("CLOSED");

        return toResponse(registrationConfigRepository.save(config));
    }

    @Transactional
    public void delete(Long id) {
        RegistrationConfig config = registrationConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RegistrationConfig", "id", id));
        registrationConfigRepository.delete(config);
    }

    private RegistrationConfigResponse toResponse(RegistrationConfig config) {
        RegistrationConfigResponse response = new RegistrationConfigResponse();
        response.setId(config.getId());
        response.setSemesterId(config.getSemester() != null ? config.getSemester().getId() : null);
        response.setSemesterName(config.getSemester() != null ? config.getSemester().getName() : null);
        response.setStartDate(config.getStartDate());
        response.setEndDate(config.getEndDate());
        response.setMinCredits(config.getMinCredits());
        response.setMaxCredits(config.getMaxCredits());
        response.setAllowAddDrop(config.getAllowAddDrop());
        response.setAddDropDeadline(config.getAddDropDeadline());
        response.setAdvisorApprovalRequired(config.getAdvisorApprovalRequired());
        response.setPaymentRequired(config.getPaymentRequired());
        response.setIsActive(config.getIsActive());
        response.setIsClosed(config.getIsClosed());
        response.setStatus(config.getStatus());
        response.setRemarks(config.getRemarks());
        response.setCreatedAt(config.getCreatedAt());
        return response;
    }

    private com.badrulamin.University_Management.entity.Semester createSemesterProxy(Long id) {
        com.badrulamin.University_Management.entity.Semester semester = new com.badrulamin.University_Management.entity.Semester();
        semester.setId(id);
        return semester;
    }
}