package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feature_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureAuditLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id")
    private Feature feature;

    @Column(name = "feature_key", nullable = false, length = 200)
    private String featureKey;

    @Column(name = "feature_name", length = 200)
    private String featureName;

    @Column(name = "previous_status")
    private Boolean previousStatus;

    @Column(name = "new_status")
    private Boolean newStatus;

    @Column(name = "changed_by", nullable = false, length = 100)
    private String changedBy;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "change_reason", length = 500)
    private String changeReason;
}
