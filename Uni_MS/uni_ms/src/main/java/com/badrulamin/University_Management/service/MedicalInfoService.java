package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.MedicalInfo;
import com.badrulamin.University_Management.repository.MedicalInfoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MedicalInfoService {

    private final MedicalInfoRepository medicalInfoRepository;

    public MedicalInfoService(MedicalInfoRepository medicalInfoRepository) {
        this.medicalInfoRepository = medicalInfoRepository;
    }

    public Page<MedicalInfo> findAll(Pageable pageable) {
        return medicalInfoRepository.findAll(pageable);
    }

    public MedicalInfo findById(Long id) {
        return medicalInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MedicalInfo not found with id: " + id));
    }

    public MedicalInfo findByStudentId(Long studentId) {
        return medicalInfoRepository.findByStudent_Id(studentId)
                .orElseThrow(() -> new RuntimeException("MedicalInfo not found for student id: " + studentId));
    }

    public boolean existsByStudentId(Long studentId) {
        return medicalInfoRepository.existsByStudent_Id(studentId);
    }

    public MedicalInfo create(MedicalInfo medicalInfo) {
        return medicalInfoRepository.save(medicalInfo);
    }

    public MedicalInfo update(Long id, MedicalInfo medicalInfo) {
        findById(id);
        medicalInfo.setId(id);
        return medicalInfoRepository.save(medicalInfo);
    }

    public void delete(Long id) {
        findById(id);
        medicalInfoRepository.deleteById(id);
    }
}
