package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.StudentEnrollment;
import com.badrulamin.University_Management.repository.StudentEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class StudentEnrollmentService {

    private final StudentEnrollmentRepository studentEnrollmentRepository;

    public Page<StudentEnrollment> findAll(Pageable pageable) {
        return studentEnrollmentRepository.findAll(pageable);
    }

    public StudentEnrollment findById(Long id) {
        return studentEnrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudentEnrollment", "id", id));
    }

    public StudentEnrollment save(StudentEnrollment studentEnrollment) {
        return studentEnrollmentRepository.save(studentEnrollment);
    }

    public StudentEnrollment update(Long id, StudentEnrollment studentEnrollment) {
        findById(id);
        studentEnrollment.setId(id);
        return studentEnrollmentRepository.save(studentEnrollment);
    }

    public void delete(Long id) {
        findById(id);
        studentEnrollmentRepository.deleteById(id);
    }
}
