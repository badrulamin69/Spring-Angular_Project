package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Certificate;
import com.badrulamin.University_Management.repository.CertificateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    public Page<Certificate> findAll(Pageable pageable) {
        return certificateRepository.findAll(pageable);
    }

    public Certificate findById(Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate", "id", id));
    }

    public Certificate create(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public Certificate update(Long id, Certificate certificate) {
        findById(id);
        certificate.setId(id);
        return certificateRepository.save(certificate);
    }

    public void delete(Long id) {
        findById(id);
        certificateRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return certificateRepository.countByStatus(status);
    }

    public long countByCertificateType(String certificateType) {
        return certificateRepository.countByCertificateType(certificateType);
    }
}
