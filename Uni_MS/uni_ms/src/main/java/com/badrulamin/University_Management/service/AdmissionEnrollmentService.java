package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionEnrollment;
import com.badrulamin.University_Management.repository.AdmissionEnrollmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AdmissionEnrollmentService {

    private final AdmissionEnrollmentRepository admissionEnrollmentRepository;

    public AdmissionEnrollmentService(AdmissionEnrollmentRepository admissionEnrollmentRepository) {
        this.admissionEnrollmentRepository = admissionEnrollmentRepository;
    }

    public Page<AdmissionEnrollment> findAll(Pageable pageable) {
        return admissionEnrollmentRepository.findAll(pageable);
    }

    public AdmissionEnrollment findById(Long id) {
        return admissionEnrollmentRepository.findById(id).orElseThrow(() -> new RuntimeException("AdmissionEnrollment not found with id: " + id));
    }

    public AdmissionEnrollment create(AdmissionEnrollment enrollment) {
        enrollment.setEnrollmentNumber("ENR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setStatus("ENROLLED");
        return admissionEnrollmentRepository.save(enrollment);
    }

    public AdmissionEnrollment update(Long id, AdmissionEnrollment enrollment) {
        AdmissionEnrollment existing = admissionEnrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AdmissionEnrollment not found with id: " + id));
        existing.setApplication(enrollment.getApplication());
        existing.setStudent(enrollment.getStudent());
        existing.setProgram(enrollment.getProgram());
        existing.setSemester(enrollment.getSemester());
        existing.setBatch(enrollment.getBatch());
        existing.setSection(enrollment.getSection());
        existing.setStatus(enrollment.getStatus());
        existing.setRemarks(enrollment.getRemarks());
        existing.setIsDocumentVerified(enrollment.getIsDocumentVerified());
        existing.setIsFeePaid(enrollment.getIsFeePaid());
        existing.setTotalFeePaid(enrollment.getTotalFeePaid());
        existing.setEnrolledBy(enrollment.getEnrolledBy());
        return admissionEnrollmentRepository.save(existing);
    }

    public void delete(Long id) {
        admissionEnrollmentRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return admissionEnrollmentRepository.countByStatus(status);
    }
}
