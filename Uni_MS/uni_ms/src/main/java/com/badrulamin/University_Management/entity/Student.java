package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    private String gender;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String studentCode;

    private LocalDate enrollmentDate;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Department department;

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
