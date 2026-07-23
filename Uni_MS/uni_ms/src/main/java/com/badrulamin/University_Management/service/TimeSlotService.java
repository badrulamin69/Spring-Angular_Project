package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.TimeSlot;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.payload.request.TimeSlotRequest;
import com.badrulamin.University_Management.payload.response.TimeSlotResponse;
import com.badrulamin.University_Management.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    public Page<TimeSlotResponse> findAll(Pageable pageable) {
        Page<TimeSlot> page = timeSlotRepository.findAll(pageable);
        return page.map(this::toResponse);
    }

    public TimeSlotResponse findById(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TimeSlot", "id", id));
        return toResponse(timeSlot);
    }

    @Transactional
    public TimeSlotResponse create(TimeSlotRequest request) {
        if (timeSlotRepository.existsByCode(request.getCode())) {
            throw new BusinessException("TimeSlot with code '" + request.getCode() + "' already exists");
        }
        if (timeSlotRepository.existsByName(request.getName())) {
            throw new BusinessException("TimeSlot with name '" + request.getName() + "' already exists");
        }

        TimeSlot timeSlot = new TimeSlot();
        mapRequestToEntity(timeSlot, request);
        TimeSlot saved = timeSlotRepository.save(timeSlot);
        return toResponse(saved);
    }

    @Transactional
    public TimeSlotResponse update(Long id, TimeSlotRequest request) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TimeSlot", "id", id));

        if (timeSlotRepository.existsByCode(request.getCode()) && !timeSlot.getCode().equals(request.getCode())) {
            throw new BusinessException("TimeSlot with code '" + request.getCode() + "' already exists");
        }
        if (timeSlotRepository.existsByName(request.getName()) && !timeSlot.getName().equals(request.getName())) {
            throw new BusinessException("TimeSlot with name '" + request.getName() + "' already exists");
        }

        mapRequestToEntity(timeSlot, request);
        TimeSlot saved = timeSlotRepository.save(timeSlot);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!timeSlotRepository.existsById(id)) {
            throw new ResourceNotFoundException("TimeSlot", "id", id);
        }
        timeSlotRepository.deleteById(id);
    }

    public List<TimeSlotResponse> findActive() {
        return timeSlotRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TimeSlotResponse> findBySlotType(String slotType) {
        return timeSlotRepository.findBySlotType(slotType)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void mapRequestToEntity(TimeSlot timeSlot, TimeSlotRequest request) {
        timeSlot.setName(request.getName());
        timeSlot.setCode(request.getCode());
        timeSlot.setStartTime(request.getStartTime());
        timeSlot.setEndTime(request.getEndTime());
        timeSlot.setSlotType(request.getSlotType());
        timeSlot.setDurationMinutes(request.getDurationMinutes());
        timeSlot.setSortOrder(request.getSortOrder());
        timeSlot.setActive(request.isActive());
        timeSlot.setRemarks(request.getRemarks());
    }

    private TimeSlotResponse toResponse(TimeSlot timeSlot) {
        TimeSlotResponse response = new TimeSlotResponse();
        response.setId(timeSlot.getId());
        response.setName(timeSlot.getName());
        response.setCode(timeSlot.getCode());
        response.setStartTime(timeSlot.getStartTime());
        response.setEndTime(timeSlot.getEndTime());
        response.setSlotType(timeSlot.getSlotType());
        response.setDurationMinutes(timeSlot.getDurationMinutes());
        response.setSortOrder(timeSlot.getSortOrder());
        response.setActive(timeSlot.isActive());
        response.setRemarks(timeSlot.getRemarks());
        response.setCreatedAt(timeSlot.getCreatedAt());
        response.setUpdatedAt(timeSlot.getUpdatedAt());
        return response;
    }
}