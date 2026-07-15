package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "programs")
public class Program extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;

    private String description;

    private String programType;

    @NotNull
    @Column(nullable = false)
    private Integer durationYears;

    private Integer totalCredits;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @JsonProperty("departmentId")
    public void setDepartmentId(Long id) {
        if (id != null) {
            this.department = new Department();
            this.department.setId(id);
        }
    }

    @JsonProperty
    public Long getDepartmentId() {
        return this.department != null ? this.department.getId() : null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administration_division_id")
    private AdministrationDivision administrationDivision;

    @JsonProperty("administrationDivisionId")
    public void setAdministrationDivisionId(Long id) {
        if (id != null) {
            this.administrationDivision = new AdministrationDivision();
            this.administrationDivision.setId(id);
        }
    }

    @JsonProperty
    public Long getAdministrationDivisionId() {
        return this.administrationDivision != null ? this.administrationDivision.getId() : null;
    }

    @Column(nullable = false)
    private boolean isActive = true;
}
