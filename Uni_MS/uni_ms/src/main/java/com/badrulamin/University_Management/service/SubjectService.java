package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Subject;
import com.badrulamin.University_Management.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public Page<Subject> findAll(Pageable pageable) {
        return subjectRepository.findAll(pageable);
    }

    public Subject findById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
    }

    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    public Subject update(Long id, Subject subject) {
        findById(id);
        subject.setId(id);
        return subjectRepository.save(subject);
    }

    public void delete(Long id) {
        findById(id);
        subjectRepository.deleteById(id);
    }
}
