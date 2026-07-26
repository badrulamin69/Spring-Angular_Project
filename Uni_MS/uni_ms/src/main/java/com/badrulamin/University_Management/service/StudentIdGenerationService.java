package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.StudentIdGeneration;
import com.badrulamin.University_Management.repository.StudentIdGenerationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class StudentIdGenerationService {

    private final StudentIdGenerationRepository repository;

    public StudentIdGenerationService(StudentIdGenerationRepository repository) {
        this.repository = repository;
    }

    public Page<StudentIdGeneration> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public StudentIdGeneration findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("StudentIdGeneration", "id", id));
    }

    public StudentIdGeneration create(StudentIdGeneration entity) {
        return repository.save(entity);
    }

    @Transactional
    public StudentIdGeneration update(Long id, StudentIdGeneration entity) {
        StudentIdGeneration existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudentIdGeneration", "id", id));
        existing.setStudentId(entity.getStudentId());
        existing.setStudentName(entity.getStudentName());
        existing.setDepartment(entity.getDepartment());
        existing.setProgram(entity.getProgram());
        existing.setBatch(entity.getBatch());
        existing.setStatus(entity.getStatus());
        existing.setIdCardNumber(entity.getIdCardNumber());
        existing.setIssuedBy(entity.getIssuedBy());
        existing.setStudent(entity.getStudent());
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}