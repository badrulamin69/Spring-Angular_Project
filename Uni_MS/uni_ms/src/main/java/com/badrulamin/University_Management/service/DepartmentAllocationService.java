package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.entity.DepartmentAllocation;
import com.badrulamin.University_Management.entity.PreAdmissionRegistration;
import com.badrulamin.University_Management.entity.AdmissionTestResult;
import com.badrulamin.University_Management.repository.DepartmentAllocationRepository;
import com.badrulamin.University_Management.repository.PreAdmissionRegistrationRepository;
import com.badrulamin.University_Management.repository.AdmissionTestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentAllocationService {

    private final DepartmentAllocationRepository repository;
    private final PreAdmissionRegistrationRepository registrationRepository;
    private final AdmissionTestResultRepository testResultRepository;
    private static final AtomicLong counter = new AtomicLong(1);

    public Page<DepartmentAllocation> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public DepartmentAllocation findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartmentAllocation", "id", id));
    }

    public DepartmentAllocation findByRegistrationId(Long registrationId) {
        return repository.findByRegistration_Id(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("DepartmentAllocation", "registrationId", registrationId));
    }

    public DepartmentAllocation save(DepartmentAllocation allocation) {
        if (allocation.getAllocationNumber() == null) {
            allocation.setAllocationNumber("ALLOC-" + Year.now().getValue() + "-" + String.format("%05d", counter.getAndIncrement()));
        }
        return repository.save(allocation);
    }

    public DepartmentAllocation update(Long id, DepartmentAllocation allocation) {
        findById(id);
        allocation.setId(id);
        return repository.save(allocation);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    public DepartmentAllocation confirm(Long id) {
        DepartmentAllocation alloc = findById(id);
        alloc.setStatus("CONFIRMED");
        alloc.setConfirmedAt(LocalDateTime.now());
        return repository.save(alloc);
    }

    public DepartmentAllocation cancel(Long id) {
        DepartmentAllocation alloc = findById(id);
        alloc.setStatus("CANCELLED");
        return repository.save(alloc);
    }

    public long countByStatus(String status) {
        return repository.countByStatus(status);
    }
}