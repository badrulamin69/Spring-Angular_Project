package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.WorkflowApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkflowApprovalRepository extends JpaRepository<WorkflowApproval, Long> {
    List<WorkflowApproval> findByEntityTypeAndEntityIdAndWorkflowStepIdOrderByActedAtDesc(
        String entityType, Long entityId, Long workflowStepId);
    List<WorkflowApproval> findByEntityTypeAndEntityIdOrderByActedAtDesc(
        String entityType, Long entityId);
    boolean existsByEntityTypeAndEntityIdAndStatus(String entityType, Long entityId, String status);
}
