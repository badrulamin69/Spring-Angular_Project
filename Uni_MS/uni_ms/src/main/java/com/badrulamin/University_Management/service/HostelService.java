package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Hostel;
import com.badrulamin.University_Management.repository.HostelRepository;
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
public class HostelService {

    private final HostelRepository hostelRepository;
    private final EntityUpdateUtil entityUpdateUtil;

    public Page<Hostel> findAll(Pageable pageable) {
        return hostelRepository.findAll(pageable);
    }

    public Hostel findById(Long id) {
        return hostelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hostel", "id", id));
    }

    @Transactional
    public Hostel save(Hostel hostel) {
        return hostelRepository.save(hostel);
    }

    @Transactional
    public Hostel update(Long id, Hostel incoming) {
        Hostel existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return hostelRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        hostelRepository.deleteById(id);
    }
}