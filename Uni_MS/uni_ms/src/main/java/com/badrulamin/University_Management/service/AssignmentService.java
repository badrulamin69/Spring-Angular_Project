package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Assignment;
import com.badrulamin.University_Management.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public Page<Assignment> findAll(Pageable pageable) {
        return assignmentRepository.findAll(pageable);
    }

    public Assignment findById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + id));
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
