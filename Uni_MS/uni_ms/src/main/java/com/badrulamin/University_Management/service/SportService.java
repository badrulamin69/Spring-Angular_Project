package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Sport;
import com.badrulamin.University_Management.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SportService {

    private final SportRepository sportRepository;

    public Page<Sport> findAll(Pageable pageable) {
        return sportRepository.findAll(pageable);
    }

    public Sport findById(Long id) {
        return sportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sport not found with id: " + id));
    }

    public Sport save(Sport sport) {
        return sportRepository.save(sport);
    }

    public Sport update(Long id, Sport sport) {
        findById(id);
        sport.setId(id);
        return sportRepository.save(sport);
    }

    public void delete(Long id) {
        findById(id);
        sportRepository.deleteById(id);
    }
}
