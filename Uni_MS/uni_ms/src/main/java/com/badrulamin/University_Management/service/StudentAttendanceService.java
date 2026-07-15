package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.StudentAttendance;
import com.badrulamin.University_Management.repository.StudentAttendanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StudentAttendanceService {

    private final StudentAttendanceRepository studentAttendanceRepository;

    public StudentAttendanceService(StudentAttendanceRepository studentAttendanceRepository) {
        this.studentAttendanceRepository = studentAttendanceRepository;
    }

    public Page<StudentAttendance> findAll(Pageable pageable) {
        return studentAttendanceRepository.findAll(pageable);
    }

    public StudentAttendance findById(Long id) {
        return studentAttendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentAttendance not found with id: " + id));
    }

    public StudentAttendance create(StudentAttendance studentAttendance) {
        return studentAttendanceRepository.save(studentAttendance);
    }

    public StudentAttendance update(Long id, StudentAttendance studentAttendance) {
        findById(id);
        studentAttendance.setId(id);
        return studentAttendanceRepository.save(studentAttendance);
    }

    public void delete(Long id) {
        findById(id);
        studentAttendanceRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return studentAttendanceRepository.countByStatus(status);
    }

    public long countByStudentId(Long studentId) {
        return studentAttendanceRepository.countByStudent_Id(studentId);
    }
}
