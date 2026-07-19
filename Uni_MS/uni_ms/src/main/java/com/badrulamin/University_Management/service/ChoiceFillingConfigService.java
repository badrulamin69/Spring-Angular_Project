package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.ChoiceFillingConfig;
import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.ChoiceFillingConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChoiceFillingConfigService {

    private final ChoiceFillingConfigRepository configRepository;

    public Page<ChoiceFillingConfig> findAll(Pageable pageable) {
        return configRepository.findAll(pageable);
    }

    public Page<ChoiceFillingConfig> findByFilters(String search, String status, Long sessionId, Pageable pageable) {
        return configRepository.findByFilters(search, status, sessionId, pageable);
    }

    public ChoiceFillingConfig findById(Long id) {
        return configRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChoiceFillingConfig", "id", id));
    }

    public ChoiceFillingConfig findActiveBySession(Long sessionId) {
        return configRepository.findBySession_IdAndIsActive(sessionId, true)
                .orElseThrow(() -> new BusinessException("No active choice filling configuration found for this session"));
    }

    public ChoiceFillingConfig findActiveConfig() {
        return configRepository.findByStatusAndIsActive("ACTIVE", true)
                .orElseThrow(() -> new BusinessException("No active choice filling configuration found"));
    }

    @Transactional
    public ChoiceFillingConfig save(ChoiceFillingConfig config) {
        if (config.getChoiceStartDate().isAfter(config.getChoiceEndDate())) {
            throw new BusinessException("Choice start date must be before end date");
        }
        if (config.getMaxChoices() < config.getMinChoices()) {
            throw new BusinessException("Maximum choices must be greater than or equal to minimum choices");
        }
        return configRepository.save(config);
    }

    @Transactional
    public ChoiceFillingConfig update(Long id, ChoiceFillingConfig updated) {
        ChoiceFillingConfig existing = findById(id);
        existing.setSession(updated.getSession());
        existing.setChoiceStartDate(updated.getChoiceStartDate());
        existing.setChoiceEndDate(updated.getChoiceEndDate());
        existing.setMaxChoices(updated.getMaxChoices());
        existing.setMinChoices(updated.getMinChoices());
        existing.setAllowEditingBeforeDeadline(updated.getAllowEditingBeforeDeadline());
        existing.setAutoLockAfterDeadline(updated.getAutoLockAfterDeadline());
        existing.setIncludeWaitingList(updated.getIncludeWaitingList());
        existing.setStatus(updated.getStatus());
        existing.setRemarks(updated.getRemarks());
        existing.setActive(updated.isActive());
        return configRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        ChoiceFillingConfig config = findById(id);
        if ("ACTIVE".equals(config.getStatus())) {
            throw new BusinessException("Cannot delete an active configuration. Deactivate it first.");
        }
        configRepository.deleteById(id);
    }

    @Transactional
    public ChoiceFillingConfig activate(Long id) {
        ChoiceFillingConfig config = findById(id);
        config.setStatus("ACTIVE");
        config.setActive(true);
        return configRepository.save(config);
    }

    @Transactional
    public ChoiceFillingConfig close(Long id) {
        ChoiceFillingConfig config = findById(id);
        config.setStatus("CLOSED");
        return configRepository.save(config);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", configRepository.count());
        stats.put("draft", configRepository.countByStatus("DRAFT"));
        stats.put("active", configRepository.countByStatus("ACTIVE"));
        stats.put("closed", configRepository.countByStatus("CLOSED"));
        return stats;
    }

    public List<ChoiceFillingConfig> findBySession(Long sessionId) {
        return configRepository.findBySession_Id(sessionId);
    }

    public boolean isWindowOpen(ChoiceFillingConfig config) {
        LocalDateTime now = LocalDateTime.now();
        return "ACTIVE".equals(config.getStatus())
                && now.isAfter(config.getChoiceStartDate())
                && now.isBefore(config.getChoiceEndDate());
    }
}
