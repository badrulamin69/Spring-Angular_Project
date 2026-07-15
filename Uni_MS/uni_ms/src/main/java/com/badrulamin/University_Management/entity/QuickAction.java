package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quick_actions")
public class QuickAction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @NotBlank
    @Column(nullable = false)
    private String title;

    private String icon;

    private String route;

    @Column(name = "permission_code")
    private String permissionCode;

    @Column(name = "order_no")
    private Integer orderNo = 0;

    private Boolean visible = true;

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
