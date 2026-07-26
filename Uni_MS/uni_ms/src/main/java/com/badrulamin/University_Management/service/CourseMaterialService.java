package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.CourseMaterial;
import com.badrulamin.University_Management.repository.CourseMaterialRepository;
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
public class CourseMaterialService {

    private final CourseMaterialRepository courseMaterialRepository;

    public Page<CourseMaterial> findAll(Pageable pageable) {
        return courseMaterialRepository.findAll(pageable);
    }

    public CourseMaterial findById(Long id) {
        return courseMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseMaterial", "id", id));
    }

    @Transactional
    public CourseMaterial save(CourseMaterial courseMaterial) {
        return courseMaterialRepository.save(courseMaterial);
    }

    @Transactional
    public CourseMaterial update(Long id, CourseMaterial courseMaterial) {
        findById(id);
        courseMaterial.setId(id);
        return courseMaterialRepository.save(courseMaterial);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        courseMaterialRepository.deleteById(id);
    }
}