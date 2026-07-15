package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Alumni;
import com.badrulamin.University_Management.repository.AlumniRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlumniService {

    private final AlumniRepository alumniRepository;

    public AlumniService(AlumniRepository alumniRepository) {
        this.alumniRepository = alumniRepository;
    }

    public Page<Alumni> findAll(Pageable pageable) {
        return alumniRepository.findAll(pageable);
    }

    public Alumni findById(Long id) {
        return alumniRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumni not found with id: " + id));
    }

    public Alumni create(Alumni alumni) {
        return alumniRepository.save(alumni);
    }

    public Alumni update(Long id, Alumni alumni) {
        findById(id);
        alumni.setId(id);
        return alumniRepository.save(alumni);
    }

    public void delete(Long id) {
        findById(id);
        alumniRepository.deleteById(id);
    }

    public long countByProgramId(Long programId) {
        return alumniRepository.countByProgram_Id(programId);
    }

    public boolean existsByStudentId(Long studentId) {
        return alumniRepository.existsByStudent_Id(studentId);
    }
}
