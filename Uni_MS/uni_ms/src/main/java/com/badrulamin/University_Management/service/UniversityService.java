package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.University;
import com.badrulamin.University_Management.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityRepository universityRepository;

    public Page<University> findAll(Pageable pageable) {
        return universityRepository.findAll(pageable);
    }

    public University findById(Long id) {
        return universityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("University not found with id: " + id));
    }

    public University save(University university) {
        return universityRepository.save(university);
    }

    public University update(Long id, University university) {
        findById(id);
        university.setId(id);
        return universityRepository.save(university);
    }

    public void delete(Long id) {
        findById(id);
        universityRepository.deleteById(id);
    }
}
