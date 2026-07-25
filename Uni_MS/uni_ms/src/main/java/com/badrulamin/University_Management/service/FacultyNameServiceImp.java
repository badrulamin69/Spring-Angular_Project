package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.FacultyName;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.FacultyNameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacultyNameServiceImp implements FacultyNameService {

    private final FacultyNameRepository facultyNameRepository;

    @Override
    @Transactional
    public FacultyName save(FacultyName facultyName) {
        return facultyNameRepository.save(facultyName);
    }

    @Override
    @Transactional
    public FacultyName update(Long id, FacultyName faculty) {
        FacultyName existing = facultyNameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FacultyName", "id", id));

        existing.setFacultyName(faculty.getFacultyName());
        existing.setDescription(faculty.getDescription());
        existing.setActive(faculty.getActive());

        return facultyNameRepository.save(existing);
    }

    @Override
    public List<FacultyName> findAll() {
        return facultyNameRepository.findAll();
    }

    @Override
    public Optional<FacultyName> findById(Long id) {
        return facultyNameRepository.findById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        facultyNameRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return facultyNameRepository.existsById(id);
    }
}