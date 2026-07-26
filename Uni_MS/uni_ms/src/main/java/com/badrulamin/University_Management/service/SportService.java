package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Sport;
import com.badrulamin.University_Management.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SportService {

    private final SportRepository sportRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Sport> findAll(Pageable pageable) {
        return sportRepository.findAll(pageable);
    }

    public Sport findById(Long id) {
        return sportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sport", "id", id));
    }

    @Transactional
    public Sport save(Sport sport) {
        return sportRepository.save(sport);
    }

    @Transactional
    public Sport update(Long id, Sport incoming) {
        Sport existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return sportRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        sportRepository.deleteById(id);
    }
}