package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboards/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
public class SuperAdminDashboardController {

    private final UniversityRepository universityRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final LoginSessionRepository loginSessionRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUniversities", universityRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalRoles", roleRepository.count());
        stats.put("totalPermissions", permissionRepository.count());
        stats.put("systemHealth", "UP");
        stats.put("activeSessions", loginSessionRepository.count());
        stats.put("recentLogins", loginHistoryRepository.count());
        stats.put("securityAlerts", 0);
        return ResponseEntity.ok(stats);
    }
}
