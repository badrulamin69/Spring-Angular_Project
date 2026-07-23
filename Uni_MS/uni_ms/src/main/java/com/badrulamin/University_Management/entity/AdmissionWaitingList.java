package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "admission_waiting_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionWaitingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id")
    private AcademicSession session;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id")
    private Program program;

    @Column(name = "shift", length = 20)
    private String shift;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id")
    private AdmissionTest test;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "DRAFT";

    @Column(name = "total_slots")
    private Integer totalSlots;

    @Column(name = "total_applicants")
    private Integer totalApplicants;

    @Column(name = "cutoff_score")
    private Double cutoffScore;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "version")
    @Version
    private Long version;

    @Column(length = 1000)
    private String remarks;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty("sessionId")
    public Long getSessionId() { return session != null ? session.getId() : null; }
    @JsonProperty("sessionId")
    public void setSessionId(Long sessionId) {
        if (sessionId != null) { AcademicSession s = new AcademicSession(); s.setId(sessionId); this.session = s; }
    }

    @JsonProperty("facultyId")
    public Long getFacultyId() { return faculty != null ? faculty.getId() : null; }
    @JsonProperty("facultyId")
    public void setFacultyId(Long facultyId) {
        if (facultyId != null) { Faculty f = new Faculty(); f.setId(facultyId); this.faculty = f; }
    }

    @JsonProperty("departmentId")
    public Long getDepartmentId() { return department != null ? department.getId() : null; }
    @JsonProperty("departmentId")
    public void setDepartmentId(Long departmentId) {
        if (departmentId != null) { Department d = new Department(); d.setId(departmentId); this.department = d; }
    }

    @JsonProperty("programId")
    public Long getProgramId() { return program != null ? program.getId() : null; }
    @JsonProperty("programId")
    public void setProgramId(Long programId) {
        if (programId != null) { Program p = new Program(); p.setId(programId); this.program = p; }
    }

    @JsonProperty("testId")
    public Long getTestId() { return test != null ? test.getId() : null; }
    @JsonProperty("testId")
    public void setTestId(Long testId) {
        if (testId != null) { AdmissionTest t = new AdmissionTest(); t.setId(testId); this.test = t; }
    }
}
