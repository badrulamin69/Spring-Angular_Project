package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Hostel;
import com.badrulamin.University_Management.repository.HostelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HostelService {

    private final HostelRepository hostelRepository;

    public Page<Hostel> findAll(Pageable pageable) {
        return hostelRepository.findAll(pageable);
    }

    public Hostel findById(Long id) {
        return hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found with id: " + id));
    }

    public Hostel save(Hostel hostel) {
        return hostelRepository.save(hostel);
    }

    public Hostel update(Long id, Hostel hostel) {
        findById(id);
        hostel.setId(id);
        return hostelRepository.save(hostel);
    }

    public void delete(Long id) {
        findById(id);
        hostelRepository.deleteById(id);
    }
}
