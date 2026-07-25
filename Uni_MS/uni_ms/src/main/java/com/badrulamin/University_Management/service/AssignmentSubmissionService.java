package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AssignmentSubmission;
import com.badrulamin.University_Management.repository.AssignmentSubmissionRepository;
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
public class AssignmentSubmissionService {

    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    public Page<AssignmentSubmission> findAll(Pageable pageable) {
        return assignmentSubmissionRepository.findAll(pageable);
    }

    public AssignmentSubmission findById(Long id) {
        return assignmentSubmissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssignmentSubmission", "id", id));
    }

    @Transactional
    public AssignmentSubmission save(AssignmentSubmission assignmentSubmission) {
        return assignmentSubmissionRepository.save(assignmentSubmission);
    }

    @Transactional
    public AssignmentSubmission update(Long id, AssignmentSubmission assignmentSubmission) {
        findById(id);
        assignmentSubmission.setId(id);
        return assignmentSubmissionRepository.save(assignmentSubmission);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        assignmentSubmissionRepository.deleteById(id);
    }
}