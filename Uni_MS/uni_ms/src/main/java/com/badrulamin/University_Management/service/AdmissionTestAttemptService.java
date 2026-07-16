package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdmissionTestAttemptService {

    private final AdmissionTestAttemptRepository repository;
    private final AdmissionTestQuestionRepository questionRepository;
    private final PreAdmissionRegistrationRepository registrationRepository;
    private final AdmissionTestRepository testRepository;
    private final AdmissionTestResultRepository testResultRepository;
    private final ObjectMapper objectMapper;

    public Page<AdmissionTestAttempt> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public AdmissionTestAttempt findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTestAttempt", "id", id));
    }

    public List<AdmissionTestAttempt> findByRegistrationId(Long registrationId) {
        return repository.findByRegistration_Id(registrationId);
    }

    public AdmissionTestAttempt startTest(Long registrationId, Long testId) {
        PreAdmissionRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("PreAdmissionRegistration", "id", registrationId));

        AdmissionTest test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTest", "id", testId));

        List<String> activeStatuses = List.of("PENDING", "IN_PROGRESS", "SUBMITTED");
        if (repository.existsByRegistration_IdAndTest_IdAndStatusIn(registrationId, testId, activeStatuses)) {
            return repository.findByRegistration_IdAndTest_Id(registrationId, testId)
                    .orElseThrow(() -> new ResourceNotFoundException("Attempt", "registration+test", registrationId));
        }

        AdmissionTestAttempt attempt = new AdmissionTestAttempt();
        attempt.setRegistration(registration);
        attempt.setTest(test);
        attempt.setStatus("IN_PROGRESS");
        attempt.setStartedAt(LocalDateTime.now());

        List<AdmissionTestQuestion> questions = questionRepository.findByTestIdOrderByCreatedAtAsc(testId);
        attempt.setTotalQuestions(questions.size());

        return repository.save(attempt);
    }

    @Transactional
    public AdmissionTestAttempt submitTest(Long attemptId, Map<String, String> answers) {
        AdmissionTestAttempt attempt = findById(attemptId);

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new RuntimeException("This test attempt is not in progress");
        }

        List<AdmissionTestQuestion> questions = questionRepository.findByTestIdOrderByCreatedAtAsc(attempt.getTest().getId());

        int correct = 0;
        double totalScore = 0;
        double maxScore = 0;

        for (AdmissionTestQuestion q : questions) {
            maxScore += q.getMarks();
            String userAnswer = answers.get(String.valueOf(q.getId()));
            if (userAnswer != null && userAnswer.equalsIgnoreCase(q.getCorrectOption())) {
                correct++;
                totalScore += q.getMarks();
            }
        }

        try {
            attempt.setAnswers(objectMapper.writeValueAsString(answers));
        } catch (JsonProcessingException e) {
            attempt.setAnswers("{}");
        }
        attempt.setCorrectAnswers(correct);
        attempt.setScore(totalScore);
        attempt.setMaxScore(maxScore);
        attempt.setPercentage(maxScore > 0 ? (totalScore / maxScore) * 100 : 0);
        attempt.setStatus("GRADED");
        attempt.setSubmittedAt(LocalDateTime.now());

        if (attempt.getStartedAt() != null) {
            attempt.setTimeTakenSeconds((int) java.time.Duration.between(attempt.getStartedAt(), LocalDateTime.now()).getSeconds());
        }

        AdmissionTestAttempt saved = repository.save(attempt);

        AdmissionTestResult result = new AdmissionTestResult();
        result.setRegistration(attempt.getRegistration());
        result.setTest(attempt.getTest());
        result.setWrittenMarks(0.0);
        result.setMcqMarks(totalScore);
        result.setVivaMarks(0.0);
        result.setStatus("SCORED");
        testResultRepository.save(result);

        attempt.getRegistration().setStatus("TEST_COMPLETED");
        registrationRepository.save(attempt.getRegistration());

        return saved;
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}