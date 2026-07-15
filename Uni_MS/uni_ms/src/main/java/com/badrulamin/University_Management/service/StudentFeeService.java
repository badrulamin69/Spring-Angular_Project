package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.StudentFee;
import com.badrulamin.University_Management.repository.StudentFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentFeeService {

    private final StudentFeeRepository studentFeeRepository;

    public Page<StudentFee> findAll(Pageable pageable) {
        return studentFeeRepository.findAll(pageable);
    }

    public StudentFee findById(Long id) {
        return studentFeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentFee not found with id: " + id));
    }

    public StudentFee save(StudentFee studentFee) {
        return studentFeeRepository.save(studentFee);
    }

    public StudentFee update(Long id, StudentFee studentFee) {
        findById(id);
        studentFee.setId(id);
        return studentFeeRepository.save(studentFee);
    }

    public void delete(Long id) {
        findById(id);
        studentFeeRepository.deleteById(id);
    }
}
