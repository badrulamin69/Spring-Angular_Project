package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Permission;
import com.badrulamin.University_Management.entity.UserPermission;
import com.badrulamin.University_Management.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;

    public Page<UserPermission> findAll(Pageable pageable) {
        return userPermissionRepository.findAll(pageable);
    }

    public UserPermission findById(Long id) {
        return userPermissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserPermission not found with id: " + id));
    }

    public UserPermission save(UserPermission userPermission) {
        return userPermissionRepository.save(userPermission);
    }

    public UserPermission update(Long id, UserPermission userPermission) {
        findById(id);
        userPermission.setId(id);
        return userPermissionRepository.save(userPermission);
    }

    public void delete(Long id) {
        findById(id);
        userPermissionRepository.deleteById(id);
    }

    public List<UserPermission> findByUserId(Long userId) {
        return userPermissionRepository.findByUser_Id(userId);
    }

    public List<UserPermission> findByPermissionId(Long permissionId) {
        return userPermissionRepository.findByPermission_Id(permissionId);
    }

    public List<Permission> getEffectivePermissions(Long userId) {
        List<UserPermission> userPermissions = userPermissionRepository.findByUser_Id(userId);

        Set<Permission> effective = new LinkedHashSet<>();

        for (UserPermission up : userPermissions) {
            if (up.isGranted()) {
                effective.add(up.getPermission());
            } else {
                effective.remove(up.getPermission());
            }
        }

        return new ArrayList<>(effective);
    }

    @Transactional
    public void bulkSave(Long userId, List<Long> permissionIds, boolean granted) {
        userPermissionRepository.deleteByUser_Id(userId);

        for (Long permissionId : permissionIds) {
            UserPermission up = new UserPermission();
            up.setUserId(userId);
            up.setPermissionId(permissionId);
            up.setGranted(granted);
            userPermissionRepository.save(up);
        }
    }
}
