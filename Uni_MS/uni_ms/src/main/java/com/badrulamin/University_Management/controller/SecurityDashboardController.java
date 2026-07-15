package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@RestController
@RequestMapping("/api/security/dashboard")
@RequiredArgsConstructor
public class SecurityDashboardController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final LoginSessionRepository loginSessionRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserPermissionRepository userPermissionRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    public ResponseEntity<Map<String, Object>> getStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActiveTrue();
        long inactiveUsers = totalUsers - activeUsers;
        long onlineUsers = loginSessionRepository.countByIsActiveTrue();
        long totalRoles = roleRepository.count();
        long totalPermissions = permissionRepository.count();
        long failedLogins = loginHistoryRepository.countBySuccessFalse();
        long lockedAccounts = userRepository.countByLockedUntilIsNotNullAndLockedUntilAfter(LocalDateTime.now());
        long activeSessions = loginSessionRepository.countByIsActiveTrue();
        long userOverrides = userPermissionRepository.count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", inactiveUsers);
        stats.put("onlineUsers", onlineUsers);
        stats.put("totalRoles", totalRoles);
        stats.put("totalPermissions", totalPermissions);
        stats.put("failedLogins", failedLogins);
        stats.put("lockedAccounts", lockedAccounts);
        stats.put("activeSessions", activeSessions);
        stats.put("userOverrides", userOverrides);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent-activities")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();

        try {
            var logs = activityLogRepository.findTop20ByOrderByCreatedAtDesc();
            for (var log : logs) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", log.getId());
                item.put("username", log.getUsername());
                item.put("action", log.getAction());
                item.put("module", log.getModule());
                item.put("description", log.getDescription());
                item.put("entityType", log.getEntityType());
                item.put("entityId", log.getEntityId());
                item.put("ipAddress", log.getIpAddress());
                item.put("createdAt", log.getCreatedAt());
                activities.add(item);
            }
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok(activities);
    }

    @GetMapping("/login-stats")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    public ResponseEntity<Map<String, Object>> getLoginStats() {
        long successfulLogins = loginHistoryRepository.countBySuccessTrue();
        long failedLogins = loginHistoryRepository.countBySuccessFalse();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long todayLogins = loginHistoryRepository.countByLoginTimestampAfter(startOfDay);

        LocalDateTime startOfWeek = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        long thisWeekLogins = loginHistoryRepository.countByLoginTimestampAfter(startOfWeek);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("successfulLogins", successfulLogins);
        stats.put("failedLogins", failedLogins);
        stats.put("todayLogins", todayLogins);
        stats.put("thisWeekLogins", thisWeekLogins);

        return ResponseEntity.ok(stats);
    }
}
