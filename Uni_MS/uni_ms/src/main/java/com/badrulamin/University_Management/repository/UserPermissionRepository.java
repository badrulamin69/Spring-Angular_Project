package com.badrulamin.University_Management.repository;

import com.badrulamin.University_Management.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    List<UserPermission> findByUser_Id(Long userId);

    List<UserPermission> findByPermission_Id(Long permissionId);

    Optional<UserPermission> findByUser_IdAndPermission_Id(Long userId, Long permissionId);

    void deleteByUser_Id(Long userId);

    long countByUser_Id(Long userId);
}
