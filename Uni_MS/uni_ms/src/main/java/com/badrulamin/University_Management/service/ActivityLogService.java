package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.ActivityLog;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public Page<ActivityLog> findAll(Pageable pageable) {
        return activityLogRepository.findAll(pageable);
    }

    public ActivityLog findById(Long id) {
        return activityLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity log not found with id: " + id));
    }

    @Transactional
    public ActivityLog save(ActivityLog activityLog) {
        return activityLogRepository.save(activityLog);
    }

    @Transactional
    public void delete(Long id) {
        activityLogRepository.deleteById(id);
    }

    public Page<ActivityLog> findByUserId(Long userId, Pageable pageable) {
        return activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<ActivityLog> findByModule(String module, Pageable pageable) {
        return activityLogRepository.findByModuleOrderByCreatedAtDesc(module, pageable);
    }

    public Page<ActivityLog> findByAction(String action, Pageable pageable) {
        return activityLogRepository.findByActionOrderByCreatedAtDesc(action, pageable);
    }

    public List<ActivityLog> findRecent(int limit) {
        return activityLogRepository.findTop20ByOrderByCreatedAtDesc();
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total", activityLogRepository.count());

        Map<String, Long> byModule = new HashMap<>();
        String[] modules = {"Students", "Finance", "Library", "Academic", "Admissions", "Examination", "HRM", "Administration", "LMS", "Hostel", "Transport", "Communication", "Reports"};
        for (String module : modules) {
            byModule.put(module, activityLogRepository.countByModule(module));
        }
        stats.put("byModule", byModule);

        Map<String, Long> byAction = new HashMap<>();
        String[] actions = {"VIEWED", "DOWNLOADED", "SEARCHED", "NAVIGATED", "CREATED", "UPDATED", "DELETED", "EXPORTED", "IMPORTED", "LOGIN", "LOGOUT"};
        for (String action : actions) {
            byAction.put(action, activityLogRepository.findByActionOrderByCreatedAtDesc(action, PageRequest.of(0, 1)).getTotalElements());
        }
        stats.put("byAction", byAction);

        return stats;
    }

    @Transactional
    public ActivityLog logActivity(String username, String action, String module,
                                   String description, String entityType, String entityId,
                                   String ipAddress, String userAgent) {
        ActivityLog activityLog = ActivityLog.builder()
                .username(username)
                .action(action)
                .module(module)
                .description(description)
                .entityType(entityType)
                .entityId(entityId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        return activityLogRepository.save(activityLog);
    }
}
