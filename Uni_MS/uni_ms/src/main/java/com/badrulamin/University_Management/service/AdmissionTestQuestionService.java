package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.entity.AdmissionTestQuestion;
import com.badrulamin.University_Management.repository.AdmissionTestQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdmissionTestQuestionService {

    private final AdmissionTestQuestionRepository repository;

    public Page<AdmissionTestQuestion> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<AdmissionTestQuestion> findByFilters(Long testId, String subject, String difficulty, String search, Pageable pageable) {
        return repository.findByFilters(testId, subject, difficulty, search, pageable);
    }

    public AdmissionTestQuestion findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTestQuestion", "id", id));
    }

    public List<AdmissionTestQuestion> findByTestId(Long testId) {
        return repository.findByTest_IdOrderByCreatedAtAsc(testId);
    }

    public AdmissionTestQuestion save(AdmissionTestQuestion question) {
        return repository.save(question);
    }

    public AdmissionTestQuestion update(Long id, AdmissionTestQuestion question) {
        AdmissionTestQuestion existing = findById(id);
        existing.setQuestionText(question.getQuestionText());
        existing.setOptionA(question.getOptionA());
        existing.setOptionB(question.getOptionB());
        existing.setOptionC(question.getOptionC());
        existing.setOptionD(question.getOptionD());
        existing.setOptionE(question.getOptionE());
        existing.setCorrectOption(question.getCorrectOption());
        existing.setMarks(question.getMarks());
        existing.setNegativeMarks(question.getNegativeMarks());
        existing.setSubject(question.getSubject());
        existing.setDifficulty(question.getDifficulty());
        existing.setQuestionType(question.getQuestionType());
        existing.setExplanation(question.getExplanation());
        existing.setIsActive(question.getIsActive());
        existing.setTest(question.getTest());
        return repository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    public long countByTestId(Long testId) {
        return repository.countByTest_Id(testId);
    }

    public List<String> findDistinctSubjects() {
        return repository.findDistinctSubjects();
    }
}