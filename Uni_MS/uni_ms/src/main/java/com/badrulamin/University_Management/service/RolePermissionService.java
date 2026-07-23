package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.RolePermission;
import com.badrulamin.University_Management.repository.RolePermissionRepository;
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
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    public Page<RolePermission> findAll(Pageable pageable) {
        return rolePermissionRepository.findAll(pageable);
    }

    public RolePermission findById(Long id) {
        return rolePermissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RolePermission", "id", id));
    }

    public RolePermission save(RolePermission rolePermission) {
        return rolePermissionRepository.save(rolePermission);
    }

    public RolePermission update(Long id, RolePermission rolePermission) {
        findById(id);
        rolePermission.setId(id);
        return rolePermissionRepository.save(rolePermission);
    }

    public void delete(Long id) {
        findById(id);
        rolePermissionRepository.deleteById(id);
    }
}