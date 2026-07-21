package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionTest;
import com.badrulamin.University_Management.repository.AdmissionTestRepository;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdmissionTestService {

    private final AdmissionTestRepository admissionTestRepository;

    public Page<AdmissionTest> findAll(Pageable pageable) {
        return admissionTestRepository.findAll(pageable);
    }

    public Page<AdmissionTest> findByFilters(String search, String status, Long facultyId, Long departmentId, LocalDate testDate, Pageable pageable) {
        return admissionTestRepository.findByFilters(search, status, facultyId, departmentId, testDate, pageable);
    }

    public AdmissionTest findById(Long id) {
        return admissionTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdmissionTest", "id", id));
    }

    public AdmissionTest save(AdmissionTest admissionTest) {
        return admissionTestRepository.save(admissionTest);
    }

    public AdmissionTest update(Long id, AdmissionTest admissionTest) {
        findById(id);
        admissionTest.setId(id);
        return admissionTestRepository.save(admissionTest);
    }

    public void delete(Long id) {
        findById(id);
        admissionTestRepository.deleteById(id);
    }

    public AdmissionTest publish(Long id) {
        AdmissionTest test = findById(id);
        test.setStatus("PUBLISHED");
        return admissionTestRepository.save(test);
    }

    public AdmissionTest close(Long id) {
        AdmissionTest test = findById(id);
        test.setStatus("CLOSED");
        return admissionTestRepository.save(test);
    }

    public List<AdmissionTest> findByStatus(String status) {
        return admissionTestRepository.findByStatus(status);
    }

    public long countByStatus(String status) {
        return admissionTestRepository.countByStatus(status);
    }

    public long countByFacultyId(Long facultyId) {
        return admissionTestRepository.countByFaculty_Id(facultyId);
    }

    public long countByDepartmentId(Long departmentId) {
        return admissionTestRepository.countByDepartment_Id(departmentId);
    }
}
