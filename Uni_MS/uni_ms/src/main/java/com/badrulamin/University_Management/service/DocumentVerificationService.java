package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.DocumentVerification;
import com.badrulamin.University_Management.repository.DocumentVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentVerificationService {

    private final DocumentVerificationRepository documentVerificationRepository;

    public Page<DocumentVerification> findAll(Pageable pageable) {
        return documentVerificationRepository.findAll(pageable);
    }

    public DocumentVerification findById(Long id) {
        return documentVerificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DocumentVerification not found with id: " + id));
    }

    public DocumentVerification save(DocumentVerification documentVerification) {
        return documentVerificationRepository.save(documentVerification);
    }

    public DocumentVerification update(Long id, DocumentVerification documentVerification) {
        findById(id);
        documentVerification.setId(id);
        return documentVerificationRepository.save(documentVerification);
    }

    public void delete(Long id) {
        findById(id);
        documentVerificationRepository.deleteById(id);
    }
}
