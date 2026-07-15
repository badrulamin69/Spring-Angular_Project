package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", unique = true, nullable = false)
    private Student student;

    @Column(length = 5)
    private String bloodGroup;

    private Double height;

    private Double weight;

    @Column(length = 2000)
    private String allergies;

    @Column(length = 2000)
    private String medications;

    @Column(length = 2000)
    private String conditions;

    @Column(length = 200)
    private String emergencyContact;

    @Column(length = 20)
    private String emergencyPhone;

    @Column(length = 200)
    private String insuranceProvider;

    @Column(length = 100)
    private String insuranceNumber;

    @Column(length = 200)
    private String doctorName;

    @Column(length = 20)
    private String doctorPhone;

    @Column(length = 2000)
    private String notes;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty("studentId")
    public Long getStudentId() { return student != null ? student.getId() : null; }
    @JsonProperty("studentId")
    public void setStudentId(Long v) { if (v != null) { Student s = new Student(); s.setId(v); this.student = s; } }
}
