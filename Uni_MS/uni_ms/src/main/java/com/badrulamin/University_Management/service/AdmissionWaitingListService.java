package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionWaitingList;
import com.badrulamin.University_Management.repository.AdmissionWaitingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdmissionWaitingListService {

    private final AdmissionWaitingListRepository waitingListRepository;

    public Page<AdmissionWaitingList> findAll(Pageable pageable) {
        return waitingListRepository.findAll(pageable);
    }

    public AdmissionWaitingList findById(Long id) {
        return waitingListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waiting list not found with id: " + id));
    }

    public AdmissionWaitingList save(AdmissionWaitingList waitingList) {
        return waitingListRepository.save(waitingList);
    }

    public AdmissionWaitingList update(Long id, AdmissionWaitingList waitingList) {
        findById(id);
        waitingList.setId(id);
        return waitingListRepository.save(waitingList);
    }

    public void delete(Long id) {
        findById(id);
        waitingListRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return waitingListRepository.countByStatus(status);
    }
}
