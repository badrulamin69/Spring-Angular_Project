package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.EmployeeAttendance;
import com.badrulamin.University_Management.repository.EmployeeAttendanceRepository;
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
public class EmployeeAttendanceService {

    private final EmployeeAttendanceRepository employeeAttendanceRepository;

    public Page<EmployeeAttendance> findAll(Pageable pageable) {
        return employeeAttendanceRepository.findAll(pageable);
    }

    public EmployeeAttendance findById(Long id) {
        return employeeAttendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeAttendance", "id", id));
    }

    @Transactional
    public EmployeeAttendance save(EmployeeAttendance employeeAttendance) {
        return employeeAttendanceRepository.save(employeeAttendance);
    }

    @Transactional
    public EmployeeAttendance update(Long id, EmployeeAttendance employeeAttendance) {
        findById(id);
        employeeAttendance.setId(id);
        return employeeAttendanceRepository.save(employeeAttendance);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        employeeAttendanceRepository.deleteById(id);
    }
}