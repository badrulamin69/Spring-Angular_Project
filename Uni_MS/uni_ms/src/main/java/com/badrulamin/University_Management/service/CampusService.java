package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Campus;
import com.badrulamin.University_Management.repository.CampusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampusService {

    private final CampusRepository campusRepository;

    public Page<Campus> findAll(Pageable pageable) {
        return campusRepository.findAll(pageable);
    }

    public Campus findById(Long id) {
        return campusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campus", "id", id));
    }

    @Transactional
    public Campus save(Campus campus) {
        return campusRepository.save(campus);
    }

    @Transactional
    public Campus update(Long id, Campus campus) {
        findById(id);
        campus.setId(id);
        return campusRepository.save(campus);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        campusRepository.deleteById(id);
    }
}