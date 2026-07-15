package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.FeatureAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeatureAuditLogRepository extends JpaRepository<FeatureAuditLog, Long> {
    List<FeatureAuditLog> findByFeatureKeyOrderByCreatedAtDesc(String featureKey);
    List<FeatureAuditLog> findAllByOrderByCreatedAtDesc();
    List<FeatureAuditLog> findTop50ByOrderByCreatedAtDesc();
}
