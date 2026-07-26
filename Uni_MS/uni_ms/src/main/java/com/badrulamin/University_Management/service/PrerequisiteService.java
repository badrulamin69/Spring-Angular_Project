package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Prerequisite;
import com.badrulamin.University_Management.repository.PrerequisiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrerequisiteService {

    private final PrerequisiteRepository prerequisiteRepository;

    public Page<Prerequisite> findAll(Pageable pageable) {
        return prerequisiteRepository.findAll(pageable);
    }

    public Prerequisite findById(Long id) {
        return prerequisiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prerequisite", "id", id));
    }

    @Transactional
    public Prerequisite save(Prerequisite prerequisite) {
        return prerequisiteRepository.save(prerequisite);
    }

    @Transactional
    public Prerequisite update(Long id, Prerequisite prerequisite) {
        findById(id);
        prerequisite.setId(id);
        return prerequisiteRepository.save(prerequisite);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        prerequisiteRepository.deleteById(id);
    }
}