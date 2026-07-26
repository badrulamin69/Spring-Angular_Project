package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Mark;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarkService {

    private final MarkRepository markRepository;

    public Page<Mark> findAll(Pageable pageable) {
        return markRepository.findAll(pageable);
    }

    public Mark findById(Long id) {
        return markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark", "id", id));
    }

    @Transactional
    public Mark save(Mark mark) {
        if (mark.getStudent() == null || mark.getExam() == null || mark.getSubject() == null) {
            throw new BusinessException("Student, exam, and subject are required");
        }
        boolean exists = markRepository.existsByStudent_IdAndExam_IdAndSubject_Id(
                mark.getStudent().getId(), mark.getExam().getId(), mark.getSubject().getId());
        if (exists) {
            throw new BusinessException("A mark already exists for this student in this exam for the given subject");
        }
        return markRepository.save(mark);
    }

    @Transactional
    public Mark update(Long id, Mark mark) {
        Mark existing = findById(id);
        mark.setId(id);
        return markRepository.save(mark);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        markRepository.deleteById(id);
    }
}