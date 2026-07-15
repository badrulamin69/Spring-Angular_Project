package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "departments")
public class Department extends BaseEntity {

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    private String description;

    @Column(name = "head_id")
    private Long head;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @JsonProperty("facultyId")
    public void setFacultyId(Long id) {
        if (id != null) {
            this.faculty = new Faculty();
            this.faculty.setId(id);
        }
    }

    @JsonProperty
    public Long getFacultyId() {
        return this.faculty != null ? this.faculty.getId() : null;
    }
}
