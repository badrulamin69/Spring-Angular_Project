package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.EmployeeAttendance;
import com.badrulamin.University_Management.repository.EmployeeAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class EmployeeAttendanceService {

    private final EmployeeAttendanceRepository employeeAttendanceRepository;

    public Page<EmployeeAttendance> findAll(Pageable pageable) {
        return employeeAttendanceRepository.findAll(pageable);
    }

    public EmployeeAttendance findById(Long id) {
        return employeeAttendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeAttendance", "id", id));
    }

    public EmployeeAttendance save(EmployeeAttendance employeeAttendance) {
        return employeeAttendanceRepository.save(employeeAttendance);
    }

    public EmployeeAttendance update(Long id, EmployeeAttendance employeeAttendance) {
        findById(id);
        employeeAttendance.setId(id);
        return employeeAttendanceRepository.save(employeeAttendance);
    }

    public void delete(Long id) {
        findById(id);
        employeeAttendanceRepository.deleteById(id);
    }
}
