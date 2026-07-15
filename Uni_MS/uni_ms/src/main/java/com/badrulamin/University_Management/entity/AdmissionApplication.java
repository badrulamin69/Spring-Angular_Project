package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "admission_application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id")
    private User applicant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id", nullable = false)
    private AcademicSession session;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campus_id")
    private Campus campus;

    @Column(nullable = false)
    private String status;

    @Column(length = 2000)
    private String remarks;

    private LocalDateTime submittedAt;

    @Column(nullable = false)
    private Boolean isSubmitted;

    @Column(nullable = false)
    private Boolean isVerified;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exam_id")
    private AdmissionTest exam;

    private Double testScore;

    private Double meritScore;

    private Integer meritPosition;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty("sessionId")
    public Long getSessionId() {
        return session != null ? session.getId() : null;
    }

    @JsonProperty("sessionId")
    public void setSessionId(Long sessionId) {
        if (sessionId != null) {
            AcademicSession s = new AcademicSession();
            s.setId(sessionId);
            this.session = s;
        }
    }

    @JsonProperty("programId")
    public Long getProgramId() {
        return program != null ? program.getId() : null;
    }

    @JsonProperty("programId")
    public void setProgramId(Long programId) {
        if (programId != null) {
            Program p = new Program();
            p.setId(programId);
            this.program = p;
        }
    }

    @JsonProperty("departmentId")
    public Long getDepartmentId() {
        return department != null ? department.getId() : null;
    }

    @JsonProperty("departmentId")
    public void setDepartmentId(Long departmentId) {
        if (departmentId != null) {
            Department d = new Department();
            d.setId(departmentId);
            this.department = d;
        }
    }

    @JsonProperty("campusId")
    public Long getCampusId() {
        return campus != null ? campus.getId() : null;
    }

    @JsonProperty("campusId")
    public void setCampusId(Long campusId) {
        if (campusId != null) {
            Campus c = new Campus();
            c.setId(campusId);
            this.campus = c;
        }
    }

    @JsonProperty("examId")
    public Long getExamId() {
        return exam != null ? exam.getId() : null;
    }

    @JsonProperty("examId")
    public void setExamId(Long examId) {
        if (examId != null) {
            AdmissionTest t = new AdmissionTest();
            t.setId(examId);
            this.exam = t;
        }
    }
}
