package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AcademicSession;
import com.badrulamin.University_Management.repository.AcademicSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcademicSessionService {

    private final AcademicSessionRepository academicSessionRepository;

    public Page<AcademicSession> findAll(Pageable pageable) {
        return academicSessionRepository.findAll(pageable);
    }

    public AcademicSession findById(Long id) {
        return academicSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicSession", "id", id));
    }

    public AcademicSession save(AcademicSession academicSession) {
        return academicSessionRepository.save(academicSession);
    }

    public AcademicSession update(Long id, AcademicSession academicSession) {
        findById(id);
        academicSession.setId(id);
        return academicSessionRepository.save(academicSession);
    }

    public void delete(Long id) {
        findById(id);
        academicSessionRepository.deleteById(id);
    }
}