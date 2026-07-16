package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.OnlineClass;
import com.badrulamin.University_Management.repository.OnlineClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class OnlineClassService {

    private final OnlineClassRepository onlineClassRepository;

    public Page<OnlineClass> findAll(Pageable pageable) {
        return onlineClassRepository.findAll(pageable);
    }

    public OnlineClass findById(Long id) {
        return onlineClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OnlineClass", "id", id));
    }

    public OnlineClass save(OnlineClass onlineClass) {
        return onlineClassRepository.save(onlineClass);
    }

    public OnlineClass update(Long id, OnlineClass onlineClass) {
        findById(id);
        onlineClass.setId(id);
        return onlineClassRepository.save(onlineClass);
    }

    public void delete(Long id) {
        findById(id);
        onlineClassRepository.deleteById(id);
    }
}
