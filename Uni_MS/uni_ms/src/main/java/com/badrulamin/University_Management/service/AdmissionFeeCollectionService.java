package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionFeeCollection;
import com.badrulamin.University_Management.repository.AdmissionFeeCollectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AdmissionFeeCollectionService {

    private final AdmissionFeeCollectionRepository repository;

    public AdmissionFeeCollectionService(AdmissionFeeCollectionRepository repository) {
        this.repository = repository;
    }

    public Page<AdmissionFeeCollection> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public AdmissionFeeCollection findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AdmissionFeeCollection", "id", id));
    }

    public AdmissionFeeCollection create(AdmissionFeeCollection entity) {
        return repository.save(entity);
    }

    public AdmissionFeeCollection update(Long id, AdmissionFeeCollection entity) {
        AdmissionFeeCollection existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionFeeCollection", "id", id));
        existing.setApplicantName(entity.getApplicantName());
        existing.setApplicantId(entity.getApplicantId());
        existing.setFeeType(entity.getFeeType());
        existing.setAmount(entity.getAmount());
        existing.setPaymentMethod(entity.getPaymentMethod());
        existing.setTransactionId(entity.getTransactionId());
        existing.setPaymentDate(entity.getPaymentDate());
        existing.setStatus(entity.getStatus());
        existing.setReceiptNumber(entity.getReceiptNumber());
        existing.setNotes(entity.getNotes());
        existing.setSession(entity.getSession());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}