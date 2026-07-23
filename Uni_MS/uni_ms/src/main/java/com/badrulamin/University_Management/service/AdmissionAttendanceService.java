package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionAttendance;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.AdmissionAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdmissionAttendanceService {

    private final AdmissionAttendanceRepository admissionAttendanceRepository;

    public Page<AdmissionAttendance> findAll(Pageable pageable) {
        return admissionAttendanceRepository.findAll(pageable);
    }

    public AdmissionAttendance findById(Long id) {
        return admissionAttendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionAttendance", "id", id));
    }

    public AdmissionAttendance save(AdmissionAttendance admissionAttendance) {
        return admissionAttendanceRepository.save(admissionAttendance);
    }

    public AdmissionAttendance update(Long id, AdmissionAttendance admissionAttendance) {
        findById(id);
        admissionAttendance.setId(id);
        return admissionAttendanceRepository.save(admissionAttendance);
    }

    public void delete(Long id) {
        findById(id);
        admissionAttendanceRepository.deleteById(id);
    }

    public List<AdmissionAttendance> findByTestId(Long testId) {
        return admissionAttendanceRepository.findByTest_Id(testId);
    }

    public Optional<AdmissionAttendance> findByTestIdAndRegistrationId(Long testId, Long registrationId) {
        return admissionAttendanceRepository.findByTest_IdAndRegistration_Id(testId, registrationId);
    }

    public long countByTestIdAndStatus(Long testId, String status) {
        return admissionAttendanceRepository.countByTest_IdAndStatus(testId, status);
    }

    public long countPresentByTestId(Long testId) {
        return admissionAttendanceRepository.countPresentByTestId(testId);
    }

    public long countAbsentByTestId(Long testId) {
        return admissionAttendanceRepository.countAbsentByTestId(testId);
    }

    public long countLateByTestId(Long testId) {
        return admissionAttendanceRepository.countLateByTestId(testId);
    }

    @Transactional
    public AdmissionAttendance markAttendance(Long testId, Long registrationId, String status, Long markedById) {
        Optional<AdmissionAttendance> existing = admissionAttendanceRepository.findByTest_IdAndRegistration_Id(testId, registrationId);

        AdmissionAttendance attendance;
        if (existing.isPresent()) {
            attendance = existing.get();
            attendance.setStatus(status);
            attendance.setMarkedById(markedById);
            if ("PRESENT".equals(status) || "LATE".equals(status)) {
                if (attendance.getCheckInTime() == null) {
                    attendance.setCheckInTime(LocalDateTime.now());
                }
            }
        } else {
            attendance = new AdmissionAttendance();
            attendance.setTestId(testId);
            attendance.setRegistrationId(registrationId);
            attendance.setStatus(status);
            attendance.setMarkedById(markedById);
            if ("PRESENT".equals(status) || "LATE".equals(status)) {
                attendance.setCheckInTime(LocalDateTime.now());
            }
        }

        return admissionAttendanceRepository.save(attendance);
    }
}