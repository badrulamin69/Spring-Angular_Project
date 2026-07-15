package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "workflows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Workflow extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @NotBlank
    @Column(name = "module_name", nullable = false)
    private String moduleName;

    @Column(name = "entity_type")
    private String entityType;

    @Column(nullable = false)
    private Boolean active = true;
}
