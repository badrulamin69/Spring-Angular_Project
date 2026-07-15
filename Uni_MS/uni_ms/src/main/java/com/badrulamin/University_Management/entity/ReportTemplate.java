package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report_templates")
public class ReportTemplate extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @NotBlank
    @Column(nullable = false)
    private String reportType;

    @Column(columnDefinition = "JSON")
    private String templateConfig;

    private Boolean isActive = true;
}
