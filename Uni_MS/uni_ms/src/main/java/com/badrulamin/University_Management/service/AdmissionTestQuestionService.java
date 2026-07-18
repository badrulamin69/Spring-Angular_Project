package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.entity.AdmissionTestQuestion;
import com.badrulamin.University_Management.repository.AdmissionTestQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdmissionTestQuestionService {

    private final AdmissionTestQuestionRepository repository;

    public Page<AdmissionTestQuestion> findAll(Pageable pageable) {
        return repository.findAll(pageable);
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
        existing.setCorrectOption(question.getCorrectOption());
        existing.setMarks(question.getMarks());
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
}