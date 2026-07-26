package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.MedicalInfo;
import com.badrulamin.University_Management.repository.MedicalInfoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
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
                .orElseThrow(() -> new ResourceNotFoundException("MedicalInfo", "id", id));
    }

    public MedicalInfo findByStudentId(Long studentId) {
        return medicalInfoRepository.findByStudent_Id(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalInfo", "studentId", studentId));
    }

    public boolean existsByStudentId(Long studentId) {
        return medicalInfoRepository.existsByStudent_Id(studentId);
    }

    public MedicalInfo create(MedicalInfo medicalInfo) {
        return medicalInfoRepository.save(medicalInfo);
    }

    @Transactional
    public MedicalInfo update(Long id, MedicalInfo medicalInfo) {
        findById(id);
        medicalInfo.setId(id);
        return medicalInfoRepository.save(medicalInfo);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        medicalInfoRepository.deleteById(id);
    }
}