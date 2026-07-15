package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowStepRepository workflowStepRepository;
    private final WorkflowApprovalRepository workflowApprovalRepository;
    private final UserRepository userRepository;

    public Page<Workflow> findAll(Pageable pageable) {
        return workflowRepository.findAll(pageable);
    }

    public Workflow findById(Long id) {
        return workflowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found: " + id));
    }

    public Workflow save(Workflow workflow) {
        return workflowRepository.save(workflow);
    }

    public Workflow update(Long id, Workflow updated) {
        Workflow existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setModuleName(updated.getModuleName());
        existing.setEntityType(updated.getEntityType());
        existing.setActive(updated.getActive());
        return workflowRepository.save(existing);
    }

    public void delete(Long id) {
        workflowRepository.deleteById(id);
    }

    public List<WorkflowStep> getSteps(Long workflowId) {
        return workflowStepRepository.findByWorkflowIdOrderByStepOrderAsc(workflowId);
    }

    public WorkflowStep addStep(Long workflowId, WorkflowStep step) {
        Workflow workflow = findById(workflowId);
        step.setWorkflow(workflow);
        return workflowStepRepository.save(step);
    }

    public WorkflowStep updateStep(Long stepId, WorkflowStep updated) {
        WorkflowStep existing = workflowStepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Workflow step not found: " + stepId));
        existing.setName(updated.getName());
        existing.setStepOrder(updated.getStepOrder());
        existing.setRequiredRole(updated.getRequiredRole());
        existing.setRequiredPermission(updated.getRequiredPermission());
        existing.setActive(updated.getActive());
        return workflowStepRepository.save(existing);
    }

    public void deleteStep(Long stepId) {
        workflowStepRepository.deleteById(stepId);
    }

    public List<WorkflowApproval> getApprovals(String entityType, Long entityId) {
        return workflowApprovalRepository.findByEntityTypeAndEntityIdOrderByActedAtDesc(entityType, entityId);
    }

    public WorkflowApproval approve(Long stepId, String entityType, Long entityId,
                                     Long approverId, String comments) {
        WorkflowStep step = workflowStepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Workflow step not found: " + stepId));
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("User not found: " + approverId));

        WorkflowApproval approval = new WorkflowApproval();
        approval.setWorkflowStep(step);
        approval.setEntityType(entityType);
        approval.setEntityId(entityId);
        approval.setApprover(approver);
        approval.setStatus("APPROVED");
        approval.setComments(comments);
        return workflowApprovalRepository.save(approval);
    }

    public WorkflowApproval reject(Long stepId, String entityType, Long entityId,
                                    Long approverId, String rejectionReason) {
        WorkflowStep step = workflowStepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Workflow step not found: " + stepId));
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("User not found: " + approverId));

        WorkflowApproval approval = new WorkflowApproval();
        approval.setWorkflowStep(step);
        approval.setEntityType(entityType);
        approval.setEntityId(entityId);
        approval.setApprover(approver);
        approval.setStatus("REJECTED");
        approval.setRejectionReason(rejectionReason);
        return workflowApprovalRepository.save(approval);
    }

    @Transactional
    public void approveLeaveRequest(Long leaveRequestId, Long approverId, String comments) {
        WorkflowApproval approval = approve(
            getOrCreateLeaveWorkflowStep(), "LEAVE_REQUEST", leaveRequestId, approverId, comments);
    }

    @Transactional
    public void rejectLeaveRequest(Long leaveRequestId, Long approverId, String reason) {
        WorkflowApproval approval = reject(
            getOrCreateLeaveWorkflowStep(), "LEAVE_REQUEST", leaveRequestId, approverId, reason);
    }

    private Long getOrCreateLeaveWorkflowStep() {
        Workflow workflow = workflowRepository.findByNameAndActiveTrue("Leave Request Approval")
            .orElseGet(() -> {
                Workflow w = new Workflow();
                w.setName("Leave Request Approval");
                w.setDescription("Multi-step leave request approval workflow");
                w.setModuleName("HRM");
                w.setEntityType("LEAVE_REQUEST");
                w.setActive(true);
                return workflowRepository.save(w);
            });

        List<WorkflowStep> steps = workflowStepRepository.findByWorkflowIdOrderByStepOrderAsc(workflow.getId());
        if (!steps.isEmpty()) {
            return steps.get(0).getId();
        }

        WorkflowStep step = new WorkflowStep();
        step.setWorkflow(workflow);
        step.setName("Department Head Approval");
        step.setStepOrder(1);
        step.setRequiredRole("ROLE_DEPT_HEAD");
        step.setRequiredPermission("LEAVE_APPROVE");
        step.setActive(true);
        return workflowStepRepository.save(step).getId();
    }
}
