package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.CourseAssignment;
import com.badrulamin.University_Management.repository.CourseAssignmentRepository;
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
public class CourseAssignmentService {

    private final CourseAssignmentRepository courseAssignmentRepository;

    public Page<CourseAssignment> findAll(Pageable pageable) {
        return courseAssignmentRepository.findAll(pageable);
    }

    public CourseAssignment findById(Long id) {
        return courseAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseAssignment", "id", id));
    }

    public CourseAssignment save(CourseAssignment courseAssignment) {
        return courseAssignmentRepository.save(courseAssignment);
    }

    public CourseAssignment update(Long id, CourseAssignment courseAssignment) {
        findById(id);
        courseAssignment.setId(id);
        return courseAssignmentRepository.save(courseAssignment);
    }

    public void delete(Long id) {
        findById(id);
        courseAssignmentRepository.deleteById(id);
    }
}