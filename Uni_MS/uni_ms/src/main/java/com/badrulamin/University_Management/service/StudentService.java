package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Student;
import com.badrulamin.University_Management.entity.Department;
import com.badrulamin.University_Management.payload.request.StudentRequest;
import com.badrulamin.University_Management.payload.response.StudentResponse;
import com.badrulamin.University_Management.repository.DepartmentRepository;
import com.badrulamin.University_Management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final EntityUpdateUtil entityUpdateUtil;

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
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    @Transactional
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    public Student update(Long id, Student incoming) {
        Student existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return studentRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        studentRepository.deleteById(id);
    }

    public StudentResponse toResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setStudentCode(student.getStudentCode());
        response.setFirstName(student.getFirstName());
        response.setLastName(student.getLastName());
        response.setEmail(student.getEmail());
        response.setPhone(student.getPhone());
        response.setGender(student.getGender());
        response.setDateOfBirth(student.getDateOfBirth());
        response.setEnrollmentDate(student.getEnrollmentDate());
        response.setStatus(student.getStatus());
        Department dept = student.getDepartment();
        response.setDepartmentId(dept != null ? dept.getId() : null);
        response.setDepartmentName(dept != null ? dept.getName() : null);
        response.setCreatedAt(student.getCreatedAt());
        return response;
    }
}