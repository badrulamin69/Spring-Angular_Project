package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Permission;
import com.badrulamin.University_Management.repository.PermissionRepository;
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
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public Page<Permission> findAll(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }

    public Permission findById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));
    }

    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    public Permission update(Long id, Permission permission) {
        findById(id);
        permission.setId(id);
        return permissionRepository.save(permission);
    }

    public void delete(Long id) {
        findById(id);
        permissionRepository.deleteById(id);
    }
}