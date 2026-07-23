package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.HostelAllocation;
import com.badrulamin.University_Management.repository.HostelAllocationRepository;
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
public class HostelAllocationService {

    private final HostelAllocationRepository hostelAllocationRepository;

    public Page<HostelAllocation> findAll(Pageable pageable) {
        return hostelAllocationRepository.findAll(pageable);
    }

    public HostelAllocation findById(Long id) {
        return hostelAllocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HostelAllocation", "id", id));
    }

    public HostelAllocation save(HostelAllocation hostelAllocation) {
        return hostelAllocationRepository.save(hostelAllocation);
    }

    public HostelAllocation update(Long id, HostelAllocation hostelAllocation) {
        findById(id);
        hostelAllocation.setId(id);
        return hostelAllocationRepository.save(hostelAllocation);
    }

    public void delete(Long id) {
        findById(id);
        hostelAllocationRepository.deleteById(id);
    }
}