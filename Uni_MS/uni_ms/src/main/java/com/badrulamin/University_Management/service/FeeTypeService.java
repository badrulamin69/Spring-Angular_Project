package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.FeeType;
import com.badrulamin.University_Management.repository.FeeTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new RuntimeException("FeeType not found with id: " + id));
    }

    public FeeType save(FeeType feeType) {
        return feeTypeRepository.save(feeType);
    }

    public FeeType update(Long id, FeeType feeType) {
        findById(id);
        feeType.setId(id);
        return feeTypeRepository.save(feeType);
    }

    public void delete(Long id) {
        findById(id);
        feeTypeRepository.deleteById(id);
    }
}
