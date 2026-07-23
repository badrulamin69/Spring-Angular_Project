package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.SemesterRegistration;
import com.badrulamin.University_Management.repository.SemesterRegistrationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class SemesterRegistrationService {

    private final SemesterRegistrationRepository semesterRegistrationRepository;

    public SemesterRegistrationService(SemesterRegistrationRepository semesterRegistrationRepository) {
        this.semesterRegistrationRepository = semesterRegistrationRepository;
    }

    public Page<SemesterRegistration> findAll(Pageable pageable) {
        return semesterRegistrationRepository.findAll(pageable);
    }

    public SemesterRegistration findById(Long id) {
        return semesterRegistrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SemesterRegistration", "id", id));
    }

    public SemesterRegistration create(SemesterRegistration semesterRegistration) {
        return semesterRegistrationRepository.save(semesterRegistration);
    }

    public SemesterRegistration update(Long id, SemesterRegistration semesterRegistration) {
        findById(id);
        semesterRegistration.setId(id);
        return semesterRegistrationRepository.save(semesterRegistration);
    }

    public void delete(Long id) {
        findById(id);
        semesterRegistrationRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return semesterRegistrationRepository.countByStatus(status);
    }
}