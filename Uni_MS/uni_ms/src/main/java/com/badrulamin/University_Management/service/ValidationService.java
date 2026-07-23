package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.CourseRegistration;
import com.badrulamin.University_Management.entity.Prerequisite;
import com.badrulamin.University_Management.entity.SubjectOffering;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.repository.CourseRegistrationRepository;
import com.badrulamin.University_Management.repository.PrerequisiteRepository;
import com.badrulamin.University_Management.repository.SubjectOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ValidationService {

    private final CourseRegistrationRepository courseRegistrationRepository;
    private final SubjectOfferingRepository subjectOfferingRepository;
    private final PrerequisiteRepository prerequisiteRepository;

    public List<String> validateRegistration(Long studentId, Long subjectId, Long semesterId, Long batchId, Integer currentTotalCredits) {
        List<String> errors = new ArrayList<>();

        checkDuplicateRegistration(studentId, subjectId, semesterId, errors);
        checkSeatAvailability(subjectId, semesterId, batchId, errors);

        return errors;
    }

    private void checkDuplicateRegistration(Long studentId, Long subjectId, Long semesterId, List<String> errors) {
        List<String> activeStatuses = List.of("SELECTED", "PENDING", "APPROVED", "REGISTERED");
        boolean exists = courseRegistrationRepository.existsByStudent_IdAndSemester_IdAndCourse_IdAndStatusIn(
                studentId, semesterId, subjectId, activeStatuses);
        if (exists) {
            errors.add("Already registered for this course in the current semester");
        }
    }

    private void checkSeatAvailability(Long subjectId, Long semesterId, Long batchId, List<String> errors) {
        SubjectOffering offering = subjectOfferingRepository.findBySubject_IdAndSemester_IdAndBatch_Id(subjectId, semesterId, batchId)
                .orElse(null);

        if (offering == null) {
            errors.add("This course is not offered in the selected semester/batch");
            return;
        }

        if (!offering.hasAvailableSeats() && !offering.hasWaitlistSpace()) {
            errors.add("No seats available and waitlist is full");
        } else if (!offering.hasAvailableSeats()) {
            errors.add("No seats available, you will be placed on the waitlist");
        }
    }

    public List<String> checkPrerequisites(Long studentId, Long subjectId) {
        List<String> errors = new ArrayList<>();
        List<Prerequisite> prerequisites = prerequisiteRepository.findBySubject_Id(subjectId);
        for (Prerequisite prereq : prerequisites) {
            if (prereq.isMandatory()) {
                errors.add("Missing mandatory prerequisite: " + prereq.getPrerequisiteSubject().getName());
            }
        }
        return errors;
    }
}