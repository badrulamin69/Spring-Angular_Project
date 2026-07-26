package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "faculties")
public class Faculty extends BaseEntity {

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;

    private String nameBn;

    private String description;

    @Column(unique = true)
    private String email;

    private String employeeCode;

    private String firstName;

    private String lastName;

    private String phone;

    private String qualification;

    private String specialization;

    private String status;

    private java.time.LocalDate joiningDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Department department;

    @Column(nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "faculty", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Department> departments;

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
}
