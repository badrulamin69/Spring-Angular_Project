package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdministrationDivision;
import com.badrulamin.University_Management.repository.AdministrationDivisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdministrationDivisionService {

    private final AdministrationDivisionRepository administrationDivisionRepository;

    public Page<AdministrationDivision> findAll(Pageable pageable) {
        return administrationDivisionRepository.findAll(pageable);
    }

    public AdministrationDivision findById(Long id) {
        return administrationDivisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdministrationDivision", "id", id));
    }

    @Transactional
    public AdministrationDivision save(AdministrationDivision administrationDivision) {
        return administrationDivisionRepository.save(administrationDivision);
    }

    @Transactional
    public AdministrationDivision update(Long id, AdministrationDivision administrationDivision) {
        findById(id);
        administrationDivision.setId(id);
        return administrationDivisionRepository.save(administrationDivision);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        administrationDivisionRepository.deleteById(id);
    }
}