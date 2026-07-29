package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.Feature;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.service.FeatureService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/features")
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureService featureService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllFeatures() {
        List<Feature> features = featureService.getAllFeatures();
        return ResponseEntity.ok(ApiResponse.success(features));
    }

    @GetMapping("/states")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFeatureStates() {
        Map<String, Boolean> states = featureService.getAllFeatureStates();
        return ResponseEntity.ok(ApiResponse.success(states));
    }

    @GetMapping("/enabled")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getEnabledFeatures() {
        List<String> enabled = featureService.getAllFeatureStates().entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(enabled));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(ApiResponse.success(featureService.getFeatureStats()));
    }

    @GetMapping("/modules")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getModules() {
        return ResponseEntity.ok(ApiResponse.success(featureService.getModules()));
    }

    @GetMapping("/categories")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(featureService.getCategories()));
    }

    @GetMapping("/module/{moduleName}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getByModule(@PathVariable String moduleName) {
        return ResponseEntity.ok(ApiResponse.success(featureService.getFeaturesByModule(moduleName)));
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.success(featureService.getFeaturesByCategory(category)));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> getAuditLogs(@RequestParam(required = false) String featureKey) {
        return ResponseEntity.ok(ApiResponse.success(featureService.getAuditLogs(featureKey)));
    }

    @GetMapping("/{featureKey}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getByKey(@PathVariable String featureKey) {
        Optional<Feature> feature = featureService.getFeatureByKey(featureKey);
        if (feature.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Feature not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(feature.get()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> createFeature(@RequestBody Feature feature) {
        try {
            Feature created = featureService.createFeature(feature, "superadmin");
            return ResponseEntity.ok(ApiResponse.success("Feature created", created));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> updateFeature(@PathVariable Long id, @RequestBody Feature feature) {
        try {
            Feature updated = featureService.updateFeature(id, feature, "superadmin");
            return ResponseEntity.ok(ApiResponse.success("Feature updated", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> deleteFeature(@PathVariable Long id) {
        try {
            featureService.deleteFeature(id);
            return ResponseEntity.ok(ApiResponse.success("Feature deleted", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{featureKey}/toggle")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> toggleFeature(
            @PathVariable String featureKey,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        boolean enabled = (Boolean) body.getOrDefault("enabled", true);
        try {
            Feature toggled = featureService.toggleFeature(featureKey, enabled, "superadmin", request);
            return ResponseEntity.ok(ApiResponse.success("Feature toggled", toggled));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/bulk-toggle")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> bulkToggle(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        String moduleName = (String) body.get("moduleName");
        boolean enabled = (Boolean) body.getOrDefault("enabled", true);
        List<Feature> toggled = featureService.bulkToggle(moduleName, enabled, "superadmin", request);
        return ResponseEntity.ok(ApiResponse.success("Module features toggled", toggled));
    }

    @GetMapping("/check/{featureKey}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> checkFeature(@PathVariable String featureKey) {
        boolean enabled = featureService.isFeatureEnabled(featureKey);
        Map<String, Object> result = new HashMap<>();
        result.put("featureKey", featureKey);
        result.put("enabled", enabled);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/reload-cache")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> reloadCache() {
        featureService.loadFeaturesIntoCache();
        return ResponseEntity.ok(ApiResponse.success("Feature cache reloaded", null));
    }
}
