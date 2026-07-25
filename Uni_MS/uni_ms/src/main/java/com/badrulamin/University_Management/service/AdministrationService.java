package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Administration;
import com.badrulamin.University_Management.repository.AdministrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdministrationService {

    private final AdministrationRepository administrationRepository;

    public Page<Administration> findAll(Pageable pageable) {
        return administrationRepository.findAll(pageable);
    }

    public Administration findById(Long id) {
        return administrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administration", "id", id));
    }

    @Transactional
    public Administration save(Administration administration) {
        return administrationRepository.save(administration);
    }

    @Transactional
    public Administration update(Long id, Administration administration) {
        findById(id);
        administration.setId(id);
        return administrationRepository.save(administration);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        administrationRepository.deleteById(id);
    }
}