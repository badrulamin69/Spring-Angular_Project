package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Faculty;
import com.badrulamin.University_Management.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Faculty> findAll(Pageable pageable) {
        return facultyRepository.findAll(pageable);
    }

    public Page<Faculty> searchFaculties(String search, Boolean isActive, Pageable pageable) {
        return facultyRepository.search(search, isActive, pageable);
    }

    public Faculty findById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));
    }

    public Faculty save(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    @Transactional
    public Faculty update(Long id, Faculty incoming) {
        Faculty existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return facultyRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        facultyRepository.deleteById(id);
    }
}