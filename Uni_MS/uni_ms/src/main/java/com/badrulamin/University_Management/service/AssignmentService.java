package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Assignment;
import com.badrulamin.University_Management.repository.AssignmentRepository;
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
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public Page<Assignment> findAll(Pageable pageable) {
        return assignmentRepository.findAll(pageable);
    }

    public Assignment findById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));
    }

    public Assignment save(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    public Assignment update(Long id, Assignment assignment) {
        findById(id);
        assignment.setId(id);
        return assignmentRepository.save(assignment);
    }

    public void delete(Long id) {
        findById(id);
        assignmentRepository.deleteById(id);
    }
}