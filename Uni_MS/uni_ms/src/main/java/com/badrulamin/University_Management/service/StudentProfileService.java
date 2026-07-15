package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.StudentProfile;
import com.badrulamin.University_Management.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;

    public Page<StudentProfile> findAll(Pageable pageable) {
        return studentProfileRepository.findAll(pageable);
    }

    public StudentProfile findById(Long id) {
        return studentProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found with id: " + id));
    }

    public StudentProfile save(StudentProfile studentProfile) {
        return studentProfileRepository.save(studentProfile);
    }

    public StudentProfile update(Long id, StudentProfile studentProfile) {
        findById(id);
        studentProfile.setId(id);
        return studentProfileRepository.save(studentProfile);
    }

    public void delete(Long id) {
        findById(id);
        studentProfileRepository.deleteById(id);
    }
}
