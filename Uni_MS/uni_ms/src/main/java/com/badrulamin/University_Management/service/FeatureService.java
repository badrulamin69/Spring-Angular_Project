package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Feature;
import com.badrulamin.University_Management.entity.FeatureAuditLog;
import com.badrulamin.University_Management.exception.FeatureDisabledException;
import com.badrulamin.University_Management.repository.FeatureAuditLogRepository;
import com.badrulamin.University_Management.repository.FeatureRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
public class FeatureService {

    @Autowired private FeatureRepository featureRepository;
    @Autowired private FeatureAuditLogRepository featureAuditLogRepository;

    private final ConcurrentHashMap<String, Boolean> featureCache = new ConcurrentHashMap<>();

    public void loadFeaturesIntoCache() {
        List<Feature> allFeatures = featureRepository.findAll();
        featureCache.clear();
        for (Feature f : allFeatures) {
            featureCache.put(f.getFeatureKey(), f.getIsEnabled());
        }
    }

    public boolean isFeatureEnabled(String featureKey) {
        Boolean enabled = featureCache.get(featureKey);
        if (enabled == null) {
            Optional<Feature> feature = featureRepository.findByFeatureKey(featureKey);
            if (feature.isPresent()) {
                enabled = feature.get().getIsEnabled();
                featureCache.put(featureKey, enabled);
            } else {
                enabled = true;
                featureCache.put(featureKey, true);
            }
        }
        return enabled;
    }

    public void checkFeatureEnabled(String featureKey) {
        if (!isFeatureEnabled(featureKey)) {
            throw new FeatureDisabledException(featureKey);
        }
    }

    public Map<String, Boolean> getAllFeatureStates() {
        if (featureCache.isEmpty()) {
            loadFeaturesIntoCache();
        }
        return new LinkedHashMap<>(featureCache);
    }

    public List<Feature> getAllFeatures() {
        return featureRepository.findAllByOrderByModuleNameAscSortOrderAsc();
    }

    public List<Feature> getFeaturesByModule(String moduleName) {
        return featureRepository.findByModuleNameOrderBySortOrderAsc(moduleName);
    }

    public List<Feature> getFeaturesByCategory(String category) {
        return featureRepository.findByCategoryOrderByModuleNameAscSortOrderAsc(category);
    }

    public Optional<Feature> getFeatureByKey(String featureKey) {
        return featureRepository.findByFeatureKey(featureKey);
    }

    public List<String> getModules() {
        return featureRepository.findDistinctModules();
    }

    public List<String> getCategories() {
        return featureRepository.findDistinctCategories();
    }

    @Transactional
    public Feature toggleFeature(String featureKey, boolean enabled, String updatedBy,
                                  HttpServletRequest request) {
        Feature feature = featureRepository.findByFeatureKey(featureKey)
                .orElseThrow(() -> new ResourceNotFoundException("Feature", "id", featureKey));

        Boolean previousStatus = feature.getIsEnabled();
        feature.setIsEnabled(enabled);
        feature.setUpdatedBy(updatedBy);
        featureRepository.save(feature);

        featureCache.put(featureKey, enabled);

        FeatureAuditLog auditLog = FeatureAuditLog.builder()
                .feature(feature)
                .featureKey(feature.getFeatureKey())
                .featureName(feature.getFeatureName())
                .previousStatus(previousStatus)
                .newStatus(enabled)
                .changedBy(updatedBy)
                .ipAddress(getClientIp(request))
                .userAgent(request != null ? request.getHeader("User-Agent") : null)
                .build();
        featureAuditLogRepository.save(auditLog);

        return feature;
    }

    @Transactional
    public List<Feature> bulkToggle(String moduleName, boolean enabled, String updatedBy,
                                     HttpServletRequest request) {
        List<Feature> features = featureRepository.findByModuleNameOrderBySortOrderAsc(moduleName);
        for (Feature feature : features) {
            Boolean previousStatus = feature.getIsEnabled();
            feature.setIsEnabled(enabled);
            feature.setUpdatedBy(updatedBy);
            featureRepository.save(feature);
            featureCache.put(feature.getFeatureKey(), enabled);

            FeatureAuditLog auditLog = FeatureAuditLog.builder()
                    .feature(feature)
                    .featureKey(feature.getFeatureKey())
                    .featureName(feature.getFeatureName())
                    .previousStatus(previousStatus)
                    .newStatus(enabled)
                    .changedBy(updatedBy)
                    .ipAddress(getClientIp(request))
                    .userAgent(request != null ? request.getHeader("User-Agent") : null)
                    .build();
            featureAuditLogRepository.save(auditLog);
        }
        return features;
    }

    @Transactional
    public Feature createFeature(Feature feature, String createdBy) {
        if (featureRepository.existsByFeatureKey(feature.getFeatureKey())) {
            throw new RuntimeException("Feature key already exists: " + feature.getFeatureKey());
        }
        feature.setCreatedBy(createdBy);
        feature.setUpdatedBy(createdBy);
        Feature saved = featureRepository.save(feature);
        featureCache.put(saved.getFeatureKey(), saved.getIsEnabled());
        return saved;
    }

    @Transactional
    public Feature updateFeature(Long id, Feature updated, String updatedBy) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feature", "id", id));
        feature.setFeatureName(updated.getFeatureName());
        feature.setModuleName(updated.getModuleName());
        feature.setCategory(updated.getCategory());
        feature.setDescription(updated.getDescription());
        feature.setSortOrder(updated.getSortOrder());
        feature.setUpdatedBy(updatedBy);
        Feature saved = featureRepository.save(feature);
        featureCache.put(saved.getFeatureKey(), saved.getIsEnabled());
        return saved;
    }

    @Transactional
    public void deleteFeature(Long id) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feature", "id", id));
        featureRepository.delete(feature);
        featureCache.remove(feature.getFeatureKey());
    }

    public List<FeatureAuditLog> getAuditLogs(String featureKey) {
        if (featureKey != null) {
            return featureAuditLogRepository.findByFeatureKeyOrderByCreatedAtDesc(featureKey);
        }
        return featureAuditLogRepository.findTop50ByOrderByCreatedAtDesc();
    }

    public Map<String, Object> getFeatureStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        List<Feature> all = featureRepository.findAll();
        stats.put("total", all.size());
        stats.put("enabled", all.stream().filter(Feature::getIsEnabled).count());
        stats.put("disabled", all.stream().filter(f -> !f.getIsEnabled()).count());
        stats.put("modules", featureRepository.findDistinctModules().size());
        return stats;
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return null;
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}