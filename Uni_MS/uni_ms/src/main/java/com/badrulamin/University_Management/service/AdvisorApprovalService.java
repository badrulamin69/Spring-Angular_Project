package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.CourseRegistration;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.request.AdvisorApprovalRequest;
import com.badrulamin.University_Management.payload.response.AdvisorApprovalResponse;
import com.badrulamin.University_Management.repository.CourseRegistrationRepository;
import com.badrulamin.University_Management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvisorApprovalService {

    private final CourseRegistrationRepository courseRegistrationRepository;
    private final UserRepository userRepository;
    private final RegistrationHistoryService historyService;

    public List<CourseRegistration> getPendingApprovals(Long semesterId) {
        return courseRegistrationRepository.findPendingAdvisorApprovals(semesterId);
    }

    @Transactional
    public AdvisorApprovalResponse processApproval(AdvisorApprovalRequest request, Long advisorId) {
        User advisor = userRepository.findById(advisorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", advisorId));

        List<CourseRegistration> registrations = new ArrayList<>();
        for (Long regId : request.getRegistrationIds()) {
            CourseRegistration reg = courseRegistrationRepository.findById(regId)
                    .orElseThrow(() -> new ResourceNotFoundException("CourseRegistration", "id", regId));

            if (!"PENDING".equals(reg.getAdvisorStatus())) {
                throw new BusinessException("Registration " + regId + " is not in PENDING status");
            }

            if ("APPROVE".equalsIgnoreCase(request.getAction())) {
                reg.setAdvisorStatus("APPROVED");
                reg.setAdvisorComments(request.getComments());
                reg.setAdvisorApprovedAt(LocalDateTime.now());
            } else if ("REJECT".equalsIgnoreCase(request.getAction())) {
                reg.setAdvisorStatus("REJECTED");
                reg.setAdvisorComments(request.getComments());
                reg.setStatus("REJECTED");
            }

            registrations.add(courseRegistrationRepository.save(reg));
        }

        if (!registrations.isEmpty()) {
            CourseRegistration first = registrations.get(0);
            historyService.recordHistory(
                first.getStudent() != null ? first.getStudent().getId() : null,
                first.getCourse() != null ? first.getCourse().getId() : null,
                first.getSemester() != null ? first.getSemester().getId() : null,
                first.getId(),
                "ADVISOR_" + request.getAction().toUpperCase(),
                "Advisor " + request.getAction().toLowerCase() + " registration. Comments: " + request.getComments(),
                advisorId,
                null
            );
        }

        AdvisorApprovalResponse response = new AdvisorApprovalResponse();
        if (!registrations.isEmpty()) {
            CourseRegistration first = registrations.get(0);
            response.setStudentId(first.getStudent() != null ? first.getStudent().getId() : null);
            response.setStudentName(first.getStudent() != null ? first.getStudent().getFirstName() + " " + first.getStudent().getLastName() : null);
            response.setStudentCode(first.getStudent() != null ? first.getStudent().getStudentCode() : null);
            response.setSemesterId(first.getSemester() != null ? first.getSemester().getId() : null);
            response.setSemesterName(first.getSemester() != null ? first.getSemester().getName() : null);
        }
        response.setApprovalAction(request.getAction());
        response.setComments(request.getComments());
        response.setProcessedRegistrationIds(request.getRegistrationIds());
        response.setProcessedAt(LocalDateTime.now());

        return response;
    }

    @Transactional
    public List<AdvisorApprovalResponse> processBulkApproval(List<Long> studentIds, Long semesterId, String action, String comments, Long advisorId) {
        List<AdvisorApprovalResponse> responses = new ArrayList<>();
        for (Long studentId : studentIds) {
            List<CourseRegistration> pending = courseRegistrationRepository.findPendingAdvisorApprovals(semesterId);
            List<Long> regIds = pending.stream()
                    .filter(r -> r.getStudent() != null && r.getStudent().getId().equals(studentId))
                    .map(CourseRegistration::getId)
                    .collect(Collectors.toList());

            if (!regIds.isEmpty()) {
                AdvisorApprovalRequest request = new AdvisorApprovalRequest();
                request.setRegistrationIds(regIds);
                request.setAction(action);
                request.setComments(comments);
                responses.add(processApproval(request, advisorId));
            }
        }
        return responses;
    }
}
