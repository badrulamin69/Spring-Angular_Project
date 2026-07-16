package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.TransportAllocation;
import com.badrulamin.University_Management.repository.TransportAllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class TransportAllocationService {

    private final TransportAllocationRepository transportAllocationRepository;

    public Page<TransportAllocation> findAll(Pageable pageable) {
        return transportAllocationRepository.findAll(pageable);
    }

    public TransportAllocation findById(Long id) {
        return transportAllocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransportAllocation", "id", id));
    }

    public TransportAllocation save(TransportAllocation transportAllocation) {
        return transportAllocationRepository.save(transportAllocation);
    }

    public TransportAllocation update(Long id, TransportAllocation transportAllocation) {
        findById(id);
        transportAllocation.setId(id);
        return transportAllocationRepository.save(transportAllocation);
    }

    public void delete(Long id) {
        findById(id);
        transportAllocationRepository.deleteById(id);
    }
}
