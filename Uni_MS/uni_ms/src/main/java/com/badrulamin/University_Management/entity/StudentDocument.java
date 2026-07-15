package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String documentType;

    @Column(nullable = false)
    private String documentName;

    @Column(length = 500)
    private String fileUrl;

    private Long fileSize;

    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    private LocalDateTime verifiedAt;

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

    @JsonProperty("verifiedById")
    public Long getVerifiedById() { return verifiedBy != null ? verifiedBy.getId() : null; }
    @JsonProperty("verifiedById")
    public void setVerifiedById(Long v) { if (v != null) { User u = new User(); u.setId(v); this.verifiedBy = u; } }
}
