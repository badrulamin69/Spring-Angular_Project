package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionMeritListEntry;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.AdmissionMeritListEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdmissionMeritListEntryService {

    private final AdmissionMeritListEntryRepository entryRepository;

    public Page<AdmissionMeritListEntry> findByMeritListId(Long meritListId, Pageable pageable) {
        return entryRepository.findByMeritList_Id(meritListId, pageable);
    }

    public Page<AdmissionMeritListEntry> findByFilters(Long meritListId, String search, String status,
            String quotaType, Pageable pageable) {
        return entryRepository.findByFilters(meritListId, search, status, quotaType, pageable);
    }

    public List<AdmissionMeritListEntry> findByMeritListIdOrdered(Long meritListId) {
        return entryRepository.findByMeritList_IdOrderByRankAsc(meritListId);
    }

    public AdmissionMeritListEntry findById(Long id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MeritListEntry", "id", id));
    }

    @Transactional
    public AdmissionMeritListEntry save(AdmissionMeritListEntry entry) {
        return entryRepository.save(entry);
    }

    @Transactional
    public AdmissionMeritListEntry update(Long id, AdmissionMeritListEntry entry) {
        findById(id);
        entry.setId(id);
        return entryRepository.save(entry);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        entryRepository.deleteById(id);
    }

    public long countByMeritListId(Long meritListId) {
        return entryRepository.countByMeritList_Id(meritListId);
    }

    public long countByMeritListIdAndStatus(Long meritListId, String status) {
        return entryRepository.countByMeritList_IdAndStatus(meritListId, status);
    }

    @Transactional
    public AdmissionMeritListEntry updateStatus(Long id, String status) {
        AdmissionMeritListEntry entry = findById(id);
        entry.setStatus(status);
        return entryRepository.save(entry);
    }
}