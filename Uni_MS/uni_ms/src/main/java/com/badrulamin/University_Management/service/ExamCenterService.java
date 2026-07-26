package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.ExamCenter;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.ExamCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamCenterService {

    private final ExamCenterRepository examCenterRepository;

    public Page<ExamCenter> findAll(Pageable pageable) {
        return examCenterRepository.findAll(pageable);
    }

    public ExamCenter findById(Long id) {
        return examCenterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExamCenter", "id", id));
    }

    @Transactional
    public ExamCenter save(ExamCenter examCenter) {
        return examCenterRepository.save(examCenter);
    }

    @Transactional
    public ExamCenter update(Long id, ExamCenter examCenter) {
        findById(id);
        examCenter.setId(id);
        return examCenterRepository.save(examCenter);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        examCenterRepository.deleteById(id);
    }

    public Optional<ExamCenter> findByCode(String code) {
        return examCenterRepository.findByCode(code);
    }

    public long countByIsActiveTrue() {
        return examCenterRepository.countByIsActiveTrue();
    }
}