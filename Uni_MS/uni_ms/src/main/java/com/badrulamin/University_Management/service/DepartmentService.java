package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Department;
import com.badrulamin.University_Management.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public Page<Department> findAll(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    public Page<Department> searchDepartments(String search, Long facultyId, Pageable pageable) {
        return departmentRepository.search(search, facultyId, pageable);
    }

    public Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    public Department update(Long id, Department department) {
        findById(id);
        department.setId(id);
        return departmentRepository.save(department);
    }

    public void delete(Long id) {
        findById(id);
        departmentRepository.deleteById(id);
    }
}
