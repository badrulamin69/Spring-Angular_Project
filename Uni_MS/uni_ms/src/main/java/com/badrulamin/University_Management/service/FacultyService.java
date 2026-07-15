package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Faculty;
import com.badrulamin.University_Management.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public Page<Faculty> findAll(Pageable pageable) {
        return facultyRepository.findAll(pageable);
    }

    public Page<Faculty> searchFaculties(String search, Boolean isActive, Pageable pageable) {
        return facultyRepository.search(search, isActive, pageable);
    }

    public Faculty findById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + id));
    }

    public Faculty save(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty update(Long id, Faculty faculty) {
        findById(id);
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }

    public void delete(Long id) {
        findById(id);
        facultyRepository.deleteById(id);
    }
}
