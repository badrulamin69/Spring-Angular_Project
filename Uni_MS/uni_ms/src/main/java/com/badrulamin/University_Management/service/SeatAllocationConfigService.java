package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.SeatAllocationConfig;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.SeatAllocationConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatAllocationConfigService {

    private final SeatAllocationConfigRepository configRepository;

    public Page<SeatAllocationConfig> findAll(Pageable pageable) {
        return configRepository.findAll(pageable);
    }

    public Page<SeatAllocationConfig> findByFilters(String search, String status, Long sessionId, Pageable pageable) {
        return configRepository.findByFilters(search, status, sessionId, pageable);
    }

    public SeatAllocationConfig findById(Long id) {
        return configRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SeatAllocationConfig", "id", id));
    }

    public SeatAllocationConfig findActiveConfig(Long sessionId) {
        return configRepository.findBySession_IdAndStatus(sessionId, "ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException("Active SeatAllocationConfig", "sessionId", sessionId));
    }

    @Transactional
    public SeatAllocationConfig create(SeatAllocationConfig config) {
        config.setStatus("DRAFT");
        return configRepository.save(config);
    }

    @Transactional
    public SeatAllocationConfig update(Long id, SeatAllocationConfig updated) {
        SeatAllocationConfig config = findById(id);
        config.setSession(updated.getSession());
        config.setAcademicYear(updated.getAcademicYear());
        config.setAllocationRound(updated.getAllocationRound());
        config.setAutoAllocation(updated.getAutoAllocation());
        config.setManualAllocation(updated.getManualAllocation());
        config.setAllocationStartDate(updated.getAllocationStartDate());
        config.setAllocationEndDate(updated.getAllocationEndDate());
        config.setAcceptDeadlineHours(updated.getAcceptDeadlineHours());
        config.setLockAfterPublish(updated.getLockAfterPublish());
        config.setEnableQuota(updated.getEnableQuota());
        config.setEnableReservedSeats(updated.getEnableReservedSeats());
        config.setRemarks(updated.getRemarks());
        return configRepository.save(config);
    }

    @Transactional
    public SeatAllocationConfig activate(Long id) {
        SeatAllocationConfig config = findById(id);
        if (config.getAllocationStartDate() == null || config.getAllocationEndDate() == null) {
            throw new BusinessException("Allocation start and end dates must be set");
        }
        config.setStatus("ACTIVE");
        return configRepository.save(config);
    }

    @Transactional
    public SeatAllocationConfig close(Long id) {
        SeatAllocationConfig config = findById(id);
        config.setStatus("CLOSED");
        return configRepository.save(config);
    }

    @Transactional
    public void delete(Long id) {
        SeatAllocationConfig config = findById(id);
        if ("ACTIVE".equals(config.getStatus())) {
            throw new BusinessException("Cannot delete an active configuration");
        }
        configRepository.deleteById(id);
    }
}
