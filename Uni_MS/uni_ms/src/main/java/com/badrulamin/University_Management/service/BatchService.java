package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Batch;
import com.badrulamin.University_Management.repository.BatchRepository;
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
public class BatchService {

    private final BatchRepository batchRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Batch> findAll(Pageable pageable) {
        return batchRepository.findAll(pageable);
    }

    public Batch findById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch", "id", id));
    }

    @Transactional
    public Batch save(Batch batch) {
        return batchRepository.save(batch);
    }

    @Transactional
    public Batch update(Long id, Batch incoming) {
        Batch existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return batchRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        batchRepository.deleteById(id);
    }
}