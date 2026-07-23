package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Payroll;
import com.badrulamin.University_Management.repository.PayrollRepository;
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
public class PayrollService {

    private final PayrollRepository payrollRepository;

    public Page<Payroll> findAll(Pageable pageable) {
        return payrollRepository.findAll(pageable);
    }

    public Payroll findById(Long id) {
        return payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll", "id", id));
    }

    public Payroll save(Payroll payroll) {
        return payrollRepository.save(payroll);
    }

    public Payroll update(Long id, Payroll payroll) {
        findById(id);
        payroll.setId(id);
        return payrollRepository.save(payroll);
    }

    public void delete(Long id) {
        findById(id);
        payrollRepository.deleteById(id);
    }
}