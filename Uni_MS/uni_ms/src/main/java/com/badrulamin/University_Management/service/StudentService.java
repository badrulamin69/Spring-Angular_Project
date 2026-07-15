package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Student;
import com.badrulamin.University_Management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Page<Student> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Page<Student> searchStudents(String keyword, String status, Long departmentId, Pageable pageable) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();
        boolean hasDept = departmentId != null;

        if (hasKeyword && (hasStatus || hasDept)) {
            return studentRepository.searchStudentsWithFilters(keyword.trim(), hasStatus ? status.trim() : null, hasDept ? departmentId : null, pageable);
        } else if (hasKeyword) {
            return studentRepository.searchStudents(keyword.trim(), pageable);
        } else if (hasStatus || hasDept) {
            return studentRepository.findAllWithFilters(hasStatus ? status.trim() : null, hasDept ? departmentId : null, pageable);
        }
        return studentRepository.findAll(pageable);
    }

    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public Student update(Long id, Student student) {
        findById(id);
        student.setId(id);
        return studentRepository.save(student);
    }

    public void delete(Long id) {
        findById(id);
        studentRepository.deleteById(id);
    }
}
