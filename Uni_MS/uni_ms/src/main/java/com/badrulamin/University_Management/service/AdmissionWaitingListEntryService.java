package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionWaitingListEntry;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.AdmissionWaitingListEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdmissionWaitingListEntryService {

    private final AdmissionWaitingListEntryRepository entryRepository;

    public Page<AdmissionWaitingListEntry> findByWaitingListId(Long waitingListId, Pageable pageable) {
        return entryRepository.findByWaitingList_Id(waitingListId, pageable);
    }

    public Page<AdmissionWaitingListEntry> findByFilters(Long waitingListId, String search,
            String status, Pageable pageable) {
        return entryRepository.findByFilters(waitingListId, search, status, pageable);
    }

    public List<AdmissionWaitingListEntry> findByWaitingListIdOrdered(Long waitingListId) {
        return entryRepository.findByWaitingList_IdOrderByRankAsc(waitingListId);
    }

    public AdmissionWaitingListEntry findById(Long id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WaitingListEntry", "id", id));
    }

    @Transactional
    public AdmissionWaitingListEntry save(AdmissionWaitingListEntry entry) {
        return entryRepository.save(entry);
    }

    @Transactional
    public AdmissionWaitingListEntry update(Long id, AdmissionWaitingListEntry entry) {
        findById(id);
        entry.setId(id);
        return entryRepository.save(entry);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        entryRepository.deleteById(id);
    }

    @Transactional
    public AdmissionWaitingListEntry updateStatus(Long id, String status) {
        AdmissionWaitingListEntry entry = findById(id);
        entry.setStatus(status);
        return entryRepository.save(entry);
    }
}