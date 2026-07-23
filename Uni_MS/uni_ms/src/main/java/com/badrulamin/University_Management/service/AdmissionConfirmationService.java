package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

    @Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class AdmissionConfirmationService {

    private final AdmissionConfirmationRepository confirmationRepository;
    private final AdmissionDocumentRepository documentRepository;
    private final DepartmentAllocationRepository allocationRepository;
    private final PreAdmissionRegistrationRepository registrationRepository;
    private final AdmissionFeeCollectionRepository feeCollectionRepository;
    private final EnrollmentService enrollmentService;

    private static final AtomicLong confirmationCounter = new AtomicLong(1);

    @Transactional
    public AdmissionConfirmation initiateConfirmation(Long allocationId) {
        DepartmentAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("DepartmentAllocation", "id", allocationId));

        if (!"ALLOCATED".equals(allocation.getStatus()) && !"CONFIRMED".equals(allocation.getStatus())) {
            throw new BusinessException("Allocation must be ALLOCATED or CONFIRMED. Current: " + allocation.getStatus());
        }

        if (confirmationRepository.existsByAllocation_Id(allocationId)) {
            throw new BusinessException("Confirmation already initiated for this allocation");
        }

        PreAdmissionRegistration reg = allocation.getRegistration();
        if (reg == null) {
            throw new IllegalArgumentException("No registration linked to this allocation");
        }

        AdmissionConfirmation confirmation = new AdmissionConfirmation();
        confirmation.setConfirmationNumber(generateConfirmationNumber());
        confirmation.setAllocation(allocation);
        confirmation.setRegistration(reg);
        confirmation.setStatus("PENDING");
        confirmation.setDocumentsSubmitted(false);
        confirmation.setDocumentsVerified(false);
        confirmation.setFeePaid(false);
        confirmation.setSession(reg.getSession());
        confirmation.setFeeAmount(0.0);

        AdmissionConfirmation saved = confirmationRepository.save(confirmation);
        log.info("Admission confirmation initiated: {} for allocation: {}", saved.getConfirmationNumber(), allocationId);
        return saved;
    }

    @Transactional
    public AdmissionConfirmation getConfirmationById(Long id) {
        return confirmationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionConfirmation", "id", id));
    }

    @Transactional
    public AdmissionConfirmation getConfirmationByAllocationId(Long allocationId) {
        return confirmationRepository.findByAllocation_Id(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionConfirmation", "allocationId", allocationId));
    }

    @Transactional
    public AdmissionConfirmation getMyConfirmation(String userEmail) {
        PreAdmissionRegistration reg = registrationRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("PreAdmissionRegistration", "email", userEmail));
        return confirmationRepository.findByRegistration_Id(reg.getId())
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionConfirmation", "registrationId", reg.getId()));
    }

    @Transactional
    public List<AdmissionDocument> submitDocuments(Long confirmationId, List<Map<String, String>> documents) {
        AdmissionConfirmation confirmation = getConfirmationById(confirmationId);

        if ("ENROLLED".equals(confirmation.getStatus())) {
            throw new BusinessException("Cannot submit documents for an already enrolled confirmation");
        }

        List<AdmissionDocument> savedDocs = new ArrayList<>();
        for (Map<String, String> docData : documents) {
            AdmissionDocument doc = new AdmissionDocument();
            doc.setConfirmation(confirmation);
            doc.setDocumentType(docData.getOrDefault("documentType", "OTHER"));
            doc.setDocumentName(docData.getOrDefault("documentName", "Untitled Document"));
            doc.setFileUrl(docData.get("fileUrl"));
            doc.setStatus("SUBMITTED");
            savedDocs.add(documentRepository.save(doc));
        }

        confirmation.setDocumentsSubmitted(true);
        confirmation.setStatus("DOCUMENTS_SUBMITTED");
        confirmationRepository.save(confirmation);

        log.info("Documents submitted for confirmation: {}", confirmation.getConfirmationNumber());
        return savedDocs;
    }

    @Transactional
    public AdmissionConfirmation verifyDocuments(Long confirmationId, boolean verified, String remarks, Long verifiedBy) {
        AdmissionConfirmation confirmation = getConfirmationById(confirmationId);

        confirmation.setDocumentsVerified(verified);
        confirmation.setDocumentsVerifiedBy(verifiedBy);
        confirmation.setDocumentsVerifiedAt(LocalDateTime.now());
        confirmation.setDocumentRemarks(remarks);

        if (verified) {
            confirmation.setStatus("DOCUMENTS_VERIFIED");
            documentRepository.findByConfirmation_IdAndStatus(confirmationId, "SUBMITTED").forEach(doc -> {
                doc.setStatus("VERIFIED");
                doc.setVerifiedBy(verifiedBy);
                doc.setVerifiedAt(LocalDateTime.now());
                documentRepository.save(doc);
            });
        } else {
            confirmation.setStatus("DOCUMENTS_REJECTED");
            documentRepository.findByConfirmation_IdAndStatus(confirmationId, "SUBMITTED").forEach(doc -> {
                doc.setStatus("REJECTED");
                doc.setRemarks(remarks);
                documentRepository.save(doc);
            });
        }

        AdmissionConfirmation saved = confirmationRepository.save(confirmation);
        log.info("Documents {} for confirmation: {}", verified ? "verified" : "rejected", confirmation.getConfirmationNumber());
        return saved;
    }

    @Transactional
    public AdmissionConfirmation payFee(Long confirmationId, Double amount, String paymentMethod, String transactionId) {
        AdmissionConfirmation confirmation = getConfirmationById(confirmationId);

        if (!"DOCUMENTS_VERIFIED".equals(confirmation.getStatus()) &&
            !"PENDING".equals(confirmation.getStatus()) &&
            !"DOCUMENTS_SUBMITTED".equals(confirmation.getStatus())) {
            throw new BusinessException("Documents must be verified before fee payment. Current: " + confirmation.getStatus());
        }

        confirmation.setFeePaid(true);
        confirmation.setFeeAmount(amount);
        confirmation.setFeePaymentMethod(paymentMethod);
        confirmation.setFeeTransactionId(transactionId);
        confirmation.setFeePaidAt(LocalDateTime.now());
        confirmation.setStatus("FEE_PAID");

        AdmissionFeeCollection feeRecord = AdmissionFeeCollection.builder()
                .applicantName(confirmation.getRegistration().getFirstName() + " " + confirmation.getRegistration().getLastName())
                .applicantId(confirmation.getRegistration().getRegistrationNumber())
                .feeType("ADMISSION_FEE")
                .amount(java.math.BigDecimal.valueOf(amount))
                .paymentMethod(paymentMethod)
                .transactionId(transactionId)
                .paymentDate(java.time.LocalDate.now())
                .status("PAID")
                .receiptNumber("RCP-" + confirmation.getConfirmationNumber())
                .session(confirmation.getSession())
                .build();
        feeCollectionRepository.save(feeRecord);

        AdmissionConfirmation saved = confirmationRepository.save(confirmation);
        log.info("Fee paid for confirmation: {} amount: {} method: {}", confirmation.getConfirmationNumber(), amount, paymentMethod);
        return saved;
    }

    @Transactional
    public Map<String, Object> confirmAdmission(Long confirmationId, Long confirmedBy) {
        AdmissionConfirmation confirmation = getConfirmationById(confirmationId);

        if (!"FEE_PAID".equals(confirmation.getStatus())) {
            throw new BusinessException("Fee must be paid before confirming admission. Current: " + confirmation.getStatus());
        }

        confirmation.setStatus("CONFIRMED");
        confirmation.setConfirmedAt(LocalDateTime.now());
        confirmation.setConfirmedBy(confirmedBy);
        confirmationRepository.save(confirmation);

        DepartmentAllocation allocation = confirmation.getAllocation();
        allocation.setStatus("CONFIRMED");
        allocation.setConfirmedAt(LocalDateTime.now());
        allocationRepository.save(allocation);

        Map<String, Object> enrollResult = enrollmentService.enrollFromAllocation(allocation.getId());

        confirmation.setStatus("ENROLLED");
        confirmationRepository.save(confirmation);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("confirmationNumber", confirmation.getConfirmationNumber());
        result.put("enrollment", enrollResult);
        result.put("message", "Admission confirmed and enrollment completed");

        log.info("Admission confirmed and enrolled: {}", confirmation.getConfirmationNumber());
        return result;
    }

    public Page<AdmissionConfirmation> findByFilters(String search, String status,
                                                      Boolean documentsVerified, Boolean feePaid,
                                                      Pageable pageable) {
        return confirmationRepository.findByFilters(search, status, documentsVerified, feePaid, pageable);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", confirmationRepository.count());
        stats.put("pending", confirmationRepository.countByStatus("PENDING"));
        stats.put("documentsSubmitted", confirmationRepository.countByStatus("DOCUMENTS_SUBMITTED"));
        stats.put("documentsVerified", confirmationRepository.countByStatus("DOCUMENTS_VERIFIED"));
        stats.put("documentsRejected", confirmationRepository.countByStatus("DOCUMENTS_REJECTED"));
        stats.put("feePaid", confirmationRepository.countByStatus("FEE_PAID"));
        stats.put("confirmed", confirmationRepository.countByStatus("CONFIRMED"));
        stats.put("enrolled", confirmationRepository.countByStatus("ENROLLED"));
        return stats;
    }

    public List<AdmissionDocument> getDocumentsByConfirmationId(Long confirmationId) {
        return documentRepository.findByConfirmation_Id(confirmationId);
    }

    private String generateConfirmationNumber() {
        long count = confirmationRepository.count() + 1;
        return "CNF-" + Year.now().getValue() + "-" + String.format("%05d", count);
    }
}