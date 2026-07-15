package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    private String firstName;

    private String lastName;

    private String phone;

    private String avatar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_role_map",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "default_role_code", length = 50)
    private String defaultRoleCode;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "email_verification_token", length = 255)
    private String emailVerificationToken;

    @Column(name = "password_reset_token", length = 255)
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @Column(name = "login_attempts")
    private Integer loginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

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

    @JsonProperty("roleIds")
    public Set<Long> getRoleIds() {
        Set<Long> ids = new HashSet<>();
        if (this.roles != null) {
            this.roles.forEach(r -> ids.add(r.getId()));
        }
        return ids;
    }

    public void addRole(Role role) {
        this.roles.add(role);
        if (this.role == null) {
            this.role = role;
        }
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        if (this.role != null && this.role.getId().equals(role.getId())) {
            this.role = this.roles.isEmpty() ? null : this.roles.iterator().next();
        }
    }

    public boolean hasRole(String roleCode) {
        if (this.role != null && this.role.getCode().equals(roleCode)) {
            return true;
        }
        return this.roles != null && this.roles.stream().anyMatch(r -> r.getCode().equals(roleCode));
    }

    public boolean isLocked() {
        return this.lockedUntil != null && this.lockedUntil.isAfter(LocalDateTime.now());
    }
}
