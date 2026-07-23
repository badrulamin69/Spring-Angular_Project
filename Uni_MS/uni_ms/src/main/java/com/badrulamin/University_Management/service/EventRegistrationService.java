package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.EventRegistration;
import com.badrulamin.University_Management.repository.EventRegistrationRepository;
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
public class EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;

    public Page<EventRegistration> findAll(Pageable pageable) {
        return eventRegistrationRepository.findAll(pageable);
    }

    public EventRegistration findById(Long id) {
        return eventRegistrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventRegistration", "id", id));
    }

    public EventRegistration save(EventRegistration eventRegistration) {
        return eventRegistrationRepository.save(eventRegistration);
    }

    public EventRegistration update(Long id, EventRegistration eventRegistration) {
        findById(id);
        eventRegistration.setId(id);
        return eventRegistrationRepository.save(eventRegistration);
    }

    public void delete(Long id) {
        findById(id);
        eventRegistrationRepository.deleteById(id);
    }
}