package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.CreditRule;
import com.badrulamin.University_Management.entity.CourseRegistration;
import com.badrulamin.University_Management.entity.Student;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.response.EligibilityCheckResponse;
import com.badrulamin.University_Management.repository.CourseRegistrationRepository;
import com.badrulamin.University_Management.repository.CreditRuleRepository;
import com.badrulamin.University_Management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EligibilityService {

    private final StudentRepository studentRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final CreditRuleRepository creditRuleRepository;
    private final RegistrationConfigService registrationConfigService;

    public EligibilityCheckResponse checkEligibility(Long studentId, Long semesterId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        EligibilityCheckResponse response = new EligibilityCheckResponse();
        response.setStudentId(studentId);
        response.setStudentName(student.getFirstName() + " " + student.getLastName());
        response.setSemesterId(semesterId);
        response.setErrors(new ArrayList<>());
        response.setWarnings(new ArrayList<>());

        try {
            var config = registrationConfigService.getActiveConfigOrThrow(semesterId);
            response.setMinCreditsRequired(config.getMinCredits());
            response.setMaxCreditsAllowed(config.getMaxCredits());

            if (config.getSemester() != null) {
                response.setSemesterName(config.getSemester().getName());
            }
        } catch (Exception e) {
            response.setErrors(List.of("Registration is not open for this semester"));
            response.setEligible(false);
            response.setStatus("INELIGIBLE");
            response.setCheckedAt(LocalDateTime.now());
            return response;
        }

        if (!"ACTIVE".equals(student.getStatus())) {
            response.getErrors().add("Student account is not active");
        }

        Integer currentCredits = courseRegistrationRepository.sumCreditHoursByStudentAndSemester(studentId, semesterId);
        response.setTotalCreditsRegistered(currentCredits);

        boolean isEligible = response.getErrors().isEmpty();
        response.setEligible(isEligible);
        response.setStatus(isEligible ? "ELIGIBLE" : "INELIGIBLE");
        response.setCheckedAt(LocalDateTime.now());

        return response;
    }
}
