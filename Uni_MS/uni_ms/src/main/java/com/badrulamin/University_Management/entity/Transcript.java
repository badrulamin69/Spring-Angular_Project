package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transcripts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transcript extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String transcriptNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private String status;

    private Double gpa;

    private Integer totalCredits;

    @Column(length = 500)
    private String remarks;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issued_by")
    private User issuedBy;

    @JsonProperty("studentId")
    public Long getStudentId() { return student != null ? student.getId() : null; }
    @JsonProperty("studentId")
    public void setStudentId(Long v) { if (v != null) { Student s = new Student(); s.setId(v); this.student = s; } }

    @JsonProperty("programId")
    public Long getProgramId() { return program != null ? program.getId() : null; }
    @JsonProperty("programId")
    public void setProgramId(Long v) { if (v != null) { Program p = new Program(); p.setId(v); this.program = p; } }

    @JsonProperty("semesterId")
    public Long getSemesterId() { return semester != null ? semester.getId() : null; }
    @JsonProperty("semesterId")
    public void setSemesterId(Long v) { if (v != null) { Semester s = new Semester(); s.setId(v); this.semester = s; } }

    @JsonProperty("issuedById")
    public Long getIssuedById() { return issuedBy != null ? issuedBy.getId() : null; }
    @JsonProperty("issuedById")
    public void setIssuedById(Long v) { if (v != null) { User u = new User(); u.setId(v); this.issuedBy = u; } }
}
