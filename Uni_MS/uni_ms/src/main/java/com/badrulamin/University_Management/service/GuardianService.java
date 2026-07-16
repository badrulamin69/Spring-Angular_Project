package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Guardian;
import com.badrulamin.University_Management.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class GuardianService {

    private final GuardianRepository guardianRepository;

    public Page<Guardian> findAll(Pageable pageable) {
        return guardianRepository.findAll(pageable);
    }

    public Guardian findById(Long id) {
        return guardianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guardian", "id", id));
    }

    public Guardian save(Guardian guardian) {
        return guardianRepository.save(guardian);
    }

    public Guardian update(Long id, Guardian guardian) {
        findById(id);
        guardian.setId(id);
        return guardianRepository.save(guardian);
    }

    public void delete(Long id) {
        findById(id);
        guardianRepository.deleteById(id);
    }
}
