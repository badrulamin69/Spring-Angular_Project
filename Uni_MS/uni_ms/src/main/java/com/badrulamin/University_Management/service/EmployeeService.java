package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Employee;
import com.badrulamin.University_Management.entity.Department;
import com.badrulamin.University_Management.payload.response.EmployeeResponse;
import com.badrulamin.University_Management.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Page<Employee> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    public Page<Employee> searchEmployees(String search, Long departmentId, String designation, String status, Pageable pageable) {
        return employeeRepository.searchEmployees(search, departmentId, designation, status, pageable);
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    @Transactional
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(Long id, Employee employee) {
        findById(id);
        employee.setId(id);
        return employeeRepository.save(employee);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        employeeRepository.deleteById(id);
    }

    public EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setEmployeeCode(employee.getEmployeeCode());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setEmail(employee.getEmail());
        response.setPhone(employee.getPhone());
        response.setDesignation(employee.getDesignation());
        Department dept = employee.getDepartment();
        response.setDepartmentId(dept != null ? dept.getId() : null);
        response.setDepartmentName(dept != null ? dept.getName() : null);
        response.setJoiningDate(employee.getJoiningDate());
        response.setSalary(employee.getSalary());
        response.setStatus(employee.getStatus());
        response.setCreatedAt(employee.getCreatedAt());
        response.setUpdatedAt(employee.getUpdatedAt());
        return response;
    }
}