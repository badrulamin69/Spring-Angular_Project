package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AcademicSession;
import com.badrulamin.University_Management.repository.AcademicSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class AcademicSessionService {

    private final AcademicSessionRepository academicSessionRepository;

    public Page<AcademicSession> findAll(Pageable pageable) {
        return academicSessionRepository.findAll(pageable);
    }

    public AcademicSession findById(Long id) {
        return academicSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AcademicSession not found with id: " + id));
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
