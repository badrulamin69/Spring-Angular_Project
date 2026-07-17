package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.FeeStructure;
import com.badrulamin.University_Management.entity.FeeType;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.FeeStructureRepository;
import com.badrulamin.University_Management.repository.FeeTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeStructureService {

    private final FeeStructureRepository feeStructureRepository;
    private final FeeTypeRepository feeTypeRepository;

    public Page<FeeStructure> findAll(Pageable pageable) {
        return feeStructureRepository.findAll(pageable);
    }

    public FeeStructure findById(Long id) {
        return feeStructureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeStructure", "id", id));
    }

    public List<FeeStructure> findBySemesterAndProgram(Long semesterId, Long programId) {
        return feeStructureRepository.findBySemesterIdAndProgramIdAndIsActiveTrue(semesterId, programId);
    }

    public List<FeeStructure> findByProgramAndAcademicYear(Long programId, String academicYear) {
        return feeStructureRepository.findByProgramIdAndAcademicYearAndIsActiveTrue(programId, academicYear);
    }

    public List<FeeStructure> findByFeeType(Long feeTypeId) {
        return feeStructureRepository.findByFeeTypeIdAndIsActiveTrue(feeTypeId);
    }

    public List<FeeStructure> findActive() {
        return feeStructureRepository.findByIsActiveTrue();
    }

    @Transactional
    public FeeStructure save(FeeStructure feeStructure) {
        if (feeStructure.getFeeType() != null && feeStructure.getFeeType().getId() != null) {
            FeeType feeType = feeTypeRepository.findById(feeStructure.getFeeType().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("FeeType", "id", feeStructure.getFeeType().getId()));
            feeStructure.setFeeType(feeType);
        }
        return feeStructureRepository.save(feeStructure);
    }

    @Transactional
    public FeeStructure update(Long id, FeeStructure feeStructure) {
        FeeStructure existing = findById(id);
        existing.setFeeType(feeStructure.getFeeType());
        existing.setProgram(feeStructure.getProgram());
        existing.setSemester(feeStructure.getSemester());
        existing.setBatch(feeStructure.getBatch());
        existing.setAmount(feeStructure.getAmount());
        existing.setDueDays(feeStructure.getDueDays());
        existing.setAcademicYear(feeStructure.getAcademicYear());
        existing.setDescription(feeStructure.getDescription());
        existing.setIsActive(feeStructure.getIsActive());
        return feeStructureRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!feeStructureRepository.existsById(id)) {
            throw new ResourceNotFoundException("FeeStructure", "id", id);
        }
        feeStructureRepository.deleteById(id);
    }
}
