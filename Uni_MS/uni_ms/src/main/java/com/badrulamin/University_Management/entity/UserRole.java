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
@Table(name = "user_roles")
public class UserRole extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

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
}
