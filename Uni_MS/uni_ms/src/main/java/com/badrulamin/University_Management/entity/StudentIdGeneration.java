package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_id_generation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentIdGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String studentName;

    private String department;

    private String program;

    private String batch;

    private String status;

    private String idCardNumber;

    private String issuedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id_ref")
    private Student student;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty("studentIdRef")
    public Long getStudentIdRef() {
        return student != null ? student.getId() : null;
    }

    @JsonProperty("studentIdRef")
    public void setStudentIdRef(Long studentIdRef) {
        if (studentIdRef != null) {
            Student s = new Student();
            s.setId(studentIdRef);
            this.student = s;
        }
    }
}
