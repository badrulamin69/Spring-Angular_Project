package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "alumni")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alumni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private LocalDate graduationDate;

    @Column(length = 100)
    private String degree;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(length = 200)
    private String currentCompany;

    @Column(length = 200)
    private String currentDesignation;

    @Column(length = 200)
    private String currentLocation;

    @Column(length = 200)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String linkedInProfile;

    private Boolean isAvailableForMentoring;

    private Boolean isAvailableForRecruitment;

    @Column(length = 500)
    private String remarks;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty("studentId")
    public Long getStudentId() { return student != null ? student.getId() : null; }
    @JsonProperty("studentId")
    public void setStudentId(Long v) { if (v != null) { Student s = new Student(); s.setId(v); this.student = s; } }

    @JsonProperty("programId")
    public Long getProgramId() { return program != null ? program.getId() : null; }
    @JsonProperty("programId")
    public void setProgramId(Long v) { if (v != null) { Program p = new Program(); p.setId(v); this.program = p; } }

    @JsonProperty("departmentId")
    public Long getDepartmentId() { return department != null ? department.getId() : null; }
    @JsonProperty("departmentId")
    public void setDepartmentId(Long v) { if (v != null) { Department d = new Department(); d.setId(v); this.department = d; } }
}
