package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Employee;
import com.badrulamin.University_Management.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
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
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee update(Long id, Employee employee) {
        findById(id);
        employee.setId(id);
        return employeeRepository.save(employee);
    }

    public void delete(Long id) {
        findById(id);
        employeeRepository.deleteById(id);
    }
}
