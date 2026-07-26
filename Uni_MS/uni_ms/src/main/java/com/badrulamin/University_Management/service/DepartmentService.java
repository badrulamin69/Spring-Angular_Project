package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Department;
import com.badrulamin.University_Management.repository.DepartmentRepository;
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
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EntityUpdateUtil entityUpdateUtil;

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

    @Transactional
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Transactional
    public Department update(Long id, Department incoming) {
        Department existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return departmentRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        departmentRepository.deleteById(id);
    }
}