package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.UserRole;
import com.badrulamin.University_Management.repository.UserRoleRepository;
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
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public Page<UserRole> findAll(Pageable pageable) {
        return userRoleRepository.findAll(pageable);
    }

    public UserRole findById(Long id) {
        return userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole", "id", id));
    }

    @Transactional
    public UserRole save(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    @Transactional
    public UserRole update(Long id, UserRole userRole) {
        findById(id);
        userRole.setId(id);
        return userRoleRepository.save(userRole);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        userRoleRepository.deleteById(id);
    }
}