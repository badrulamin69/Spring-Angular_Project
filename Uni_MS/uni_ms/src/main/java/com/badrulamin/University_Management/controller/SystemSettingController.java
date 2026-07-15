package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.SystemSetting;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/system-settings")
public class SystemSettingController {

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    @GetMapping("/dropdowns")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDropdowns() {
        List<SystemSetting> settings = systemSettingRepository.findByIsPublicTrue();
        Map<String, Object> result = new LinkedHashMap<>();
        for (SystemSetting s : settings) {
            if (s.getSettingKey().startsWith("dropdown.")) {
                String key = s.getSettingKey().replace("dropdown.", "");
                List<String> values = Arrays.stream(s.getSettingValue().split(","))
                        .map(String::trim)
                        .filter(v -> !v.isEmpty())
                        .collect(Collectors.toList());
                result.put(key, values);
            }
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(ApiResponse.success(systemSettingRepository.findAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return systemSettingRepository.findById(id)
                .map(s -> ResponseEntity.ok(ApiResponse.success(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    public ResponseEntity<?> create(@RequestBody SystemSetting setting) {
        return ResponseEntity.ok(ApiResponse.success("Setting created", systemSettingRepository.save(setting)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SystemSetting setting) {
        setting.setId(id);
        return ResponseEntity.ok(ApiResponse.success("Setting updated", systemSettingRepository.save(setting)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SETTINGS_MANAGE')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        systemSettingRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Setting deleted", null));
    }
}
