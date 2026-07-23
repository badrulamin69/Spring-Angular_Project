package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.AuditLog;
import com.badrulamin.University_Management.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public Page<AuditLog> findAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    public AuditLog findById(Long id) {
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog", "id", id));
    }

    public AuditLog save(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    public AuditLog update(Long id, AuditLog auditLog) {
        findById(id);
        auditLog.setId(id);
        return auditLogRepository.save(auditLog);
    }

    public void delete(Long id) {
        findById(id);
        auditLogRepository.deleteById(id);
    }
}