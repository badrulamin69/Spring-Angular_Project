package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.FeeType;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.FeeTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeTypeService {

    private final FeeTypeRepository feeTypeRepository;

    public Page<FeeType> findAll(Pageable pageable) {
        return feeTypeRepository.findAll(pageable);
    }

    public FeeType findById(Long id) {
        return feeTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeType", "id", id));
    }

    public FeeType findByCode(String code) {
        return feeTypeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("FeeType", "code", code));
    }

    public List<FeeType> findByCategory(String category) {
        return feeTypeRepository.findByCategory(category);
    }

    public List<FeeType> findActive() {
        return feeTypeRepository.findByIsActiveTrue();
    }

    @Transactional
    public FeeType save(FeeType feeType) {
        if (feeTypeRepository.existsByCode(feeType.getCode())) {
            throw new BusinessException("FeeType with code '" + feeType.getCode() + "' already exists");
        }
        if (feeTypeRepository.existsByName(feeType.getName())) {
            throw new BusinessException("FeeType with name '" + feeType.getName() + "' already exists");
        }
        return feeTypeRepository.save(feeType);
    }

    @Transactional
    public FeeType update(Long id, FeeType feeType) {
        FeeType existing = findById(id);
        existing.setName(feeType.getName());
        existing.setCode(feeType.getCode());
        existing.setCategory(feeType.getCategory());
        existing.setDescription(feeType.getDescription());
        existing.setDefaultAmount(feeType.getDefaultAmount());
        existing.setIsActive(feeType.getIsActive());
        return feeTypeRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!feeTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("FeeType", "id", id);
        }
        feeTypeRepository.deleteById(id);
    }
}
