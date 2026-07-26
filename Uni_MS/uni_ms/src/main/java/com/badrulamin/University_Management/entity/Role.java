package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;

    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Integer level = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_role_id")
    @JsonIgnore
    private Role parentRole;



    @JsonProperty("parentRoleId")
    public Long getParentRoleId() {
        return parentRole != null ? parentRole.getId() : null;
    }

    @JsonProperty("parentRoleName")
    public String getParentRoleName() {
        return parentRole != null ? parentRole.getName() : null;
    }
}
