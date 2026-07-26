package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.DocumentVerification;
import com.badrulamin.University_Management.repository.DocumentVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentVerificationService {

    private final DocumentVerificationRepository documentVerificationRepository;

    public Page<DocumentVerification> findAll(Pageable pageable) {
        return documentVerificationRepository.findAll(pageable);
    }

    public DocumentVerification findById(Long id) {
        return documentVerificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DocumentVerification", "id", id));
    }

    @Transactional
    public DocumentVerification save(DocumentVerification documentVerification) {
        return documentVerificationRepository.save(documentVerification);
    }

    @Transactional
    public DocumentVerification update(Long id, DocumentVerification documentVerification) {
        findById(id);
        documentVerification.setId(id);
        return documentVerificationRepository.save(documentVerification);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        documentVerificationRepository.deleteById(id);
    }
}