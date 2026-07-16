package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Batch;
import com.badrulamin.University_Management.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;

    public Page<Batch> findAll(Pageable pageable) {
        return batchRepository.findAll(pageable);
    }

    public Batch findById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch", "id", id));
    }

    public Batch save(Batch batch) {
        return batchRepository.save(batch);
    }

    public Batch update(Long id, Batch batch) {
        findById(id);
        batch.setId(id);
        return batchRepository.save(batch);
    }

    public void delete(Long id) {
        findById(id);
        batchRepository.deleteById(id);
    }
}
