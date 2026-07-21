package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionTest;
import com.badrulamin.University_Management.entity.EligibilityVerification;
import com.badrulamin.University_Management.entity.PreAdmissionRegistration;
import com.badrulamin.University_Management.entity.SeatAllocation;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.AdmissionTestRepository;
import com.badrulamin.University_Management.repository.EligibilityVerificationRepository;
import com.badrulamin.University_Management.repository.PreAdmissionRegistrationRepository;
import com.badrulamin.University_Management.repository.SeatAllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EligibilityVerificationService {

    private final EligibilityVerificationRepository eligibilityVerificationRepository;
    private final AdmissionTestRepository admissionTestRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final PreAdmissionRegistrationRepository preAdmissionRegistrationRepository;
    private final NotificationHelper notificationHelper;

    public Page<EligibilityVerification> findAll(Pageable pageable) {
        return eligibilityVerificationRepository.findAll(pageable);
    }

    public EligibilityVerification findById(Long id) {
        return eligibilityVerificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EligibilityVerification", "id", id));
    }

    public EligibilityVerification save(EligibilityVerification eligibilityVerification) {
        return eligibilityVerificationRepository.save(eligibilityVerification);
    }

    public EligibilityVerification update(Long id, EligibilityVerification eligibilityVerification) {
        findById(id);
        eligibilityVerification.setId(id);
        return eligibilityVerificationRepository.save(eligibilityVerification);
    }

    public void delete(Long id) {
        findById(id);
        eligibilityVerificationRepository.deleteById(id);
    }

    public List<EligibilityVerification> findByTestId(Long testId) {
        return eligibilityVerificationRepository.findByTest_Id(testId);
    }

    public List<EligibilityVerification> findByRegistrationId(Long registrationId) {
        return eligibilityVerificationRepository.findByRegistration_Id(registrationId);
    }

    public List<EligibilityVerification> findByTestIdAndStatus(Long testId, String status) {
        return eligibilityVerificationRepository.findByTest_IdAndStatus(testId, status);
    }

    @Transactional
    public EligibilityVerification verifyEligibility(Long testId, Long registrationId, boolean eligible, String verifiedBy, String remarks) {
        AdmissionTest test = admissionTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTest", "id", testId));
        PreAdmissionRegistration registration = preAdmissionRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("PreAdmissionRegistration", "id", registrationId));

        Optional<EligibilityVerification> existing = eligibilityVerificationRepository.findByTest_IdAndRegistration_Id(testId, registrationId);

        EligibilityVerification verification;
        if (existing.isPresent()) {
            verification = existing.get();
        } else {
            verification = new EligibilityVerification();
            verification.setTest(test);
            verification.setRegistration(registration);
        }

        verification.setStatus(eligible ? "ELIGIBLE" : "INELIGIBLE");
        verification.setVerifiedBy(verifiedBy);
        verification.setVerifiedAt(LocalDateTime.now());
        verification.setRemarks(remarks);
        verification.setSscGpaVerified(registration.getSscGpa() != null);
        verification.setHscGpaVerified(registration.getHscGpa() != null);
        verification.setDocumentsVerified(true);

        EligibilityVerification saved = eligibilityVerificationRepository.save(verification);
        notificationHelper.eligibilityVerified(registrationId, test.getName(), eligible);
        return saved;
    }

    @Transactional
    public List<EligibilityVerification> autoVerifyAll(Long testId) {
        AdmissionTest test = admissionTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTest", "id", testId));

        List<SeatAllocation> seatAllocations = seatAllocationRepository.findByTest_Id(testId);
        List<EligibilityVerification> verifications = new ArrayList<>();

        for (SeatAllocation allocation : seatAllocations) {
            PreAdmissionRegistration registration = allocation.getRegistration();

            Optional<EligibilityVerification> existing = eligibilityVerificationRepository.findByTest_IdAndRegistration_Id(testId, registration.getId());

            EligibilityVerification verification;
            if (existing.isPresent()) {
                verification = existing.get();
            } else {
                verification = new EligibilityVerification();
                verification.setTest(test);
                verification.setRegistration(registration);
            }

            boolean sscPresent = registration.getSscGpa() != null;
            boolean hscPresent = registration.getHscGpa() != null;

            verification.setSscGpaVerified(sscPresent);
            verification.setHscGpaVerified(hscPresent);
            verification.setDocumentsVerified(true);
            verification.setVerifiedBy("SYSTEM");
            verification.setVerifiedAt(LocalDateTime.now());

            if (sscPresent && hscPresent) {
                verification.setStatus("ELIGIBLE");
                verification.setRemarks("Auto-verified: Both SSC and HSC GPA present");
            } else {
                verification.setStatus("INELIGIBLE");
                verification.setRemarks("Auto-verified: Missing " + (!sscPresent ? "SSC GPA" : "HSC GPA"));
            }

            verifications.add(eligibilityVerificationRepository.save(verification));
        }

        return verifications;
    }

    public Map<String, Object> getStats(Long testId) {
        admissionTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTest", "id", testId));

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", eligibilityVerificationRepository.countByTest_Id(testId));
        stats.put("eligible", eligibilityVerificationRepository.countByTest_IdAndStatus(testId, "ELIGIBLE"));
        stats.put("ineligible", eligibilityVerificationRepository.countByTest_IdAndStatus(testId, "INELIGIBLE"));
        stats.put("pending", eligibilityVerificationRepository.countByTest_IdAndStatus(testId, "PENDING"));
        return stats;
    }
}
