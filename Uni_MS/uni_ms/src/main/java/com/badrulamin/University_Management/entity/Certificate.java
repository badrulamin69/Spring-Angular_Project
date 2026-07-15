package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String certificateNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String certificateType;

    private LocalDateTime issuedAt;

    private LocalDateTime validUntil;

    @Column(nullable = false)
    private String status;

    @Column(length = 500)
    private String purpose;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issued_by")
    private User issuedBy;

    private Boolean isDownloaded;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty("studentId")
    public Long getStudentId() { return student != null ? student.getId() : null; }
    @JsonProperty("studentId")
    public void setStudentId(Long v) { if (v != null) { Student s = new Student(); s.setId(v); this.student = s; } }

    @JsonProperty("issuedById")
    public Long getIssuedById() { return issuedBy != null ? issuedBy.getId() : null; }
    @JsonProperty("issuedById")
    public void setIssuedById(Long v) { if (v != null) { User u = new User(); u.setId(v); this.issuedBy = u; } }
}
