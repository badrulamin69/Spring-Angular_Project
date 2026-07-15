package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_permissions")
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @JsonProperty("roleId")
    public void setRoleId(Long id) {
        if (id != null) {
            this.role = new Role();
            this.role.setId(id);
        }
    }

    @JsonProperty
    public Long getRoleId() {
        return this.role != null ? this.role.getId() : null;
    }

    @JsonProperty("permissionId")
    public void setPermissionId(Long id) {
        if (id != null) {
            this.permission = new Permission();
            this.permission.setId(id);
        }
    }

    @JsonProperty
    public Long getPermissionId() {
        return this.permission != null ? this.permission.getId() : null;
    }
}
