package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "workflow_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStep extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "required_role")
    private String requiredRole;

    @Column(name = "required_permission")
    private String requiredPermission;

    @Column(nullable = false)
    private Boolean active = true;
}
