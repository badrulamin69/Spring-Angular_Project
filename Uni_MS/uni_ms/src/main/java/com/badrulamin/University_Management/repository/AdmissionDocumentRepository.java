package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.AdmissionDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionDocumentRepository extends JpaRepository<AdmissionDocument, Long> {

    List<AdmissionDocument> findByConfirmation_Id(Long confirmationId);

    List<AdmissionDocument> findByConfirmation_IdAndStatus(Long confirmationId, String status);

    long countByConfirmation_Id(Long confirmationId);

    long countByConfirmation_IdAndStatus(Long confirmationId, String status);

    long countByConfirmation_IdAndDocumentType(Long confirmationId, String documentType);
}
