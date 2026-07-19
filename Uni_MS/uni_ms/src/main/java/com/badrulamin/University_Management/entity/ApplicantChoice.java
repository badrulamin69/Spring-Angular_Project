package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applicant_choices",
       uniqueConstraints = @UniqueConstraint(columnNames = {"submission_id", "program_id"}))
public class ApplicantChoice extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "submission_id", nullable = false)
    private ApplicantChoiceSubmission submission;

    @JsonProperty("submissionId")
    public Long getSubmissionId() { return submission != null ? submission.getId() : null; }
    @JsonProperty("submissionId")
    public void setSubmissionId(Long submissionId) {
        if (submissionId != null) {
            ApplicantChoiceSubmission s = new ApplicantChoiceSubmission();
            s.setId(submissionId);
            this.submission = s;
        }
    }

    @NotNull
    @Column(name = "priority", nullable = false)
    private Integer priority;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @JsonProperty("facultyId")
    public Long getFacultyId() { return faculty != null ? faculty.getId() : null; }
    @JsonProperty("facultyId")
    public void setFacultyId(Long facultyId) {
        if (facultyId != null) {
            Faculty f = new Faculty();
            f.setId(facultyId);
            this.faculty = f;
        }
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @JsonProperty("departmentId")
    public Long getDepartmentId() { return department != null ? department.getId() : null; }
    @JsonProperty("departmentId")
    public void setDepartmentId(Long departmentId) {
        if (departmentId != null) {
            Department d = new Department();
            d.setId(departmentId);
            this.department = d;
        }
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @JsonProperty("programId")
    public Long getProgramId() { return program != null ? program.getId() : null; }
    @JsonProperty("programId")
    public void setProgramId(Long programId) {
        if (programId != null) {
            Program p = new Program();
            p.setId(programId);
            this.program = p;
        }
    }

    @Column(name = "faculty_name", length = 200)
    private String facultyName;

    @Column(name = "department_name", length = 200)
    private String departmentName;

    @Column(name = "program_name", length = 200)
    private String programName;

    @Column(name = "shift", length = 20)
    private String shift;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";
}
