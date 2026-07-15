package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    Optional<Workflow> findByModuleNameAndEntityTypeAndActiveTrue(String moduleName, String entityType);
    Optional<Workflow> findByNameAndActiveTrue(String name);
}
