package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_permissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "permission_id"})
})
public class UserPermission extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Column(nullable = false)
    private boolean granted = true;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "overridden_by_id")
    private User overriddenBy;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @JsonProperty("userId")
    public void setUserId(Long id) {
        if (id != null) {
            this.user = new User();
            this.user.setId(id);
        }
    }

    @JsonProperty
    public Long getUserId() {
        return this.user != null ? this.user.getId() : null;
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

    @JsonProperty("overriddenById")
    public void setOverriddenById(Long id) {
        if (id != null) {
            this.overriddenBy = new User();
            this.overriddenBy.setId(id);
        }
    }

    @JsonProperty
    public Long getOverriddenById() {
        return this.overriddenBy != null ? this.overriddenBy.getId() : null;
    }
}
