package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Announcement;
import com.badrulamin.University_Management.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public Page<Announcement> findAll(Pageable pageable) {
        return announcementRepository.findAll(pageable);
    }

    public Announcement findById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));
    }

    public Announcement save(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    public Announcement update(Long id, Announcement announcement) {
        findById(id);
        announcement.setId(id);
        return announcementRepository.save(announcement);
    }

    public void delete(Long id) {
        findById(id);
        announcementRepository.deleteById(id);
    }
}
