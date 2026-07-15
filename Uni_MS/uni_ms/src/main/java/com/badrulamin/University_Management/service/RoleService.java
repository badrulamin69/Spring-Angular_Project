package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Role;
import com.badrulamin.University_Management.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Page<Role> findAll(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public Role update(Long id, Role role) {
        findById(id);
        role.setId(id);
        return roleRepository.save(role);
    }

    public void delete(Long id) {
        findById(id);
        roleRepository.deleteById(id);
    }

    public List<Role> getRootRoles() {
        return roleRepository.findByParentRoleIsNullOrderByLevelAsc();
    }

    public List<Role> getChildRoles(Long parentId) {
        Role parent = findById(parentId);
        return roleRepository.findByParentRoleOrderByLevelAsc(parent);
    }

    public List<Role> getRolesByLevel(Integer level) {
        return roleRepository.findByLevelOrderByLevelAsc(level);
    }

    public List<Role> getRoleHierarchy() {
        List<Role> roots = getRootRoles();
        List<Role> flat = new ArrayList<>();
        for (Role root : roots) {
            flattenHierarchy(root, flat, 0);
        }
        return flat;
    }

    private void flattenHierarchy(Role role, List<Role> flat, int depth) {
        role.setLevel(depth);
        flat.add(role);
        List<Role> children = roleRepository.findByParentRoleOrderByLevelAsc(role);
        for (Role child : children) {
            flattenHierarchy(child, flat, depth + 1);
        }
    }
}
