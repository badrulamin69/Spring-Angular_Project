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
@Table(name = "teachers")
public class Teacher extends BaseEntity {

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

    private String gender;

    private LocalDate dateOfBirth;

    private String bloodGroup;

    private String nationality;

    private String religion;

    private String maritalStatus;

    private String photo;

    private String nationalId;

    private String passport;

    private String emergencyContact;

    private String presentAddress;

    private String permanentAddress;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String teacherCode;

    private String uniqueCode;

    private LocalDate joiningDate;

    private String employmentStatus;

    private String employmentType;

    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    @JsonIgnore
    private Faculty faculty;

    private String officeRoom;

    private String campus;

    private String highestDegree;

    private String university;

    private String specialization;

    private String experience;

    private String certifications;

    private String assignedCourses;

    private String sections;

    private String semester;

    private String creditLoad;

    private String googleScholar;

    private String orcid;

    private String salaryGrade;

    private Double basicSalary;

    private String bankInformation;

    private String taxId;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

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
}
