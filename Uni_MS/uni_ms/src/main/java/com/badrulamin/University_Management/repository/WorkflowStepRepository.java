package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Long> {
    List<WorkflowStep> findByWorkflow_IdOrderByStepOrderAsc(Long workflowId);
}
