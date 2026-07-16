package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Curriculum;
import com.badrulamin.University_Management.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;

    public Page<Curriculum> findAll(Pageable pageable) {
        return curriculumRepository.findAll(pageable);
    }

    public Curriculum findById(Long id) {
        return curriculumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", id));
    }

    public Curriculum save(Curriculum curriculum) {
        return curriculumRepository.save(curriculum);
    }

    public Curriculum update(Long id, Curriculum curriculum) {
        findById(id);
        curriculum.setId(id);
        return curriculumRepository.save(curriculum);
    }

    public void delete(Long id) {
        findById(id);
        curriculumRepository.deleteById(id);
    }
}
