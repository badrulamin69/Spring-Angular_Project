package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.config.EntityUpdateUtil;
import com.badrulamin.University_Management.entity.Announcement;
import com.badrulamin.University_Management.repository.AnnouncementRepository;
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
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final EntityUpdateUtil entityUpdateUtil;

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

    @Transactional
    public Announcement update(Long id, Announcement incoming) {
        Announcement existing = findById(id);
        entityUpdateUtil.merge(incoming, existing);
        return announcementRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        announcementRepository.deleteById(id);
    }
}