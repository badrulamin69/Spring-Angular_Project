package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_approvals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowApproval extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_step_id", nullable = false)
    private WorkflowStep workflowStep;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @NotBlank
    @Column(nullable = false)
    private String status;

    private String comments;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "acted_at")
    private LocalDateTime actedAt;
}
