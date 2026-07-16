package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AdmissionMeritList;
import com.badrulamin.University_Management.repository.AdmissionMeritListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class AdmissionMeritListService {

    private final AdmissionMeritListRepository meritListRepository;

    public Page<AdmissionMeritList> findAll(Pageable pageable) {
        return meritListRepository.findAll(pageable);
    }

    public AdmissionMeritList findById(Long id) {
        return meritListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merit list", "id", id));
    }

    public AdmissionMeritList save(AdmissionMeritList meritList) {
        return meritListRepository.save(meritList);
    }

    public AdmissionMeritList update(Long id, AdmissionMeritList meritList) {
        findById(id);
        meritList.setId(id);
        return meritListRepository.save(meritList);
    }

    public void delete(Long id) {
        findById(id);
        meritListRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return meritListRepository.countByStatus(status);
    }
}
