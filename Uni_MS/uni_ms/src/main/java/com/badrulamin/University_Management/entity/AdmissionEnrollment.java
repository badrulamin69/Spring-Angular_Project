package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "admission_enrollment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String enrollmentNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id")
    private AdmissionApplication application;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "section_id")
    private Section section;

    @Column(nullable = false)
    private String status;

    private LocalDateTime enrolledAt;

    @Column(length = 2000)
    private String remarks;

    private Boolean isDocumentVerified;

    private Boolean isFeePaid;

    private Double totalFeePaid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enrolled_by")
    private User enrolledBy;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty("applicationId")
    public Long getApplicationId() {
        return application != null ? application.getId() : null;
    }

    @JsonProperty("applicationId")
    public void setApplicationId(Long applicationId) {
        if (applicationId != null) {
            AdmissionApplication a = new AdmissionApplication();
            a.setId(applicationId);
            this.application = a;
        }
    }

    @JsonProperty("studentId")
    public Long getStudentId() {
        return student != null ? student.getId() : null;
    }

    @JsonProperty("studentId")
    public void setStudentId(Long studentId) {
        if (studentId != null) {
            Student s = new Student();
            s.setId(studentId);
            this.student = s;
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

    @JsonProperty("semesterId")
    public Long getSemesterId() {
        return semester != null ? semester.getId() : null;
    }

    @JsonProperty("semesterId")
    public void setSemesterId(Long semesterId) {
        if (semesterId != null) {
            Semester s = new Semester();
            s.setId(semesterId);
            this.semester = s;
        }
    }

    @JsonProperty("batchId")
    public Long getBatchId() {
        return batch != null ? batch.getId() : null;
    }

    @JsonProperty("batchId")
    public void setBatchId(Long batchId) {
        if (batchId != null) {
            Batch b = new Batch();
            b.setId(batchId);
            this.batch = b;
        }
    }

    @JsonProperty("sectionId")
    public Long getSectionId() {
        return section != null ? section.getId() : null;
    }

    @JsonProperty("sectionId")
    public void setSectionId(Long sectionId) {
        if (sectionId != null) {
            Section s = new Section();
            s.setId(sectionId);
            this.section = s;
        }
    }

    @JsonProperty("enrolledById")
    public Long getEnrolledById() {
        return enrolledBy != null ? enrolledBy.getId() : null;
    }

    @JsonProperty("enrolledById")
    public void setEnrolledById(Long enrolledById) {
        if (enrolledById != null) {
            User u = new User();
            u.setId(enrolledById);
            this.enrolledBy = u;
        }
    }
}
