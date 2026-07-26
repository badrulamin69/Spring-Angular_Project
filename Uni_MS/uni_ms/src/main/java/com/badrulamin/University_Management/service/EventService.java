package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Event;
import com.badrulamin.University_Management.repository.EventRepository;
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
public class EventService {

    private final EventRepository eventRepository;

    public Page<Event> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
    }

    @Transactional
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public Event update(Long id, Event event) {
        findById(id);
        event.setId(id);
        return eventRepository.save(event);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        eventRepository.deleteById(id);
    }
}