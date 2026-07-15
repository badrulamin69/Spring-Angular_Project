package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_promotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_semester_id")
    private Semester fromSemester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_semester_id")
    private Semester toSemester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_batch_id")
    private Batch fromBatch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_batch_id")
    private Batch toBatch;

    private LocalDate promotionDate;

    @Column(nullable = false)
    private String status;

    @Column(length = 500)
    private String remarks;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty("studentId")
    public Long getStudentId() { return student != null ? student.getId() : null; }
    @JsonProperty("studentId")
    public void setStudentId(Long v) { if (v != null) { Student s = new Student(); s.setId(v); this.student = s; } }

    @JsonProperty("fromSemesterId")
    public Long getFromSemesterId() { return fromSemester != null ? fromSemester.getId() : null; }
    @JsonProperty("fromSemesterId")
    public void setFromSemesterId(Long v) { if (v != null) { Semester s = new Semester(); s.setId(v); this.fromSemester = s; } }

    @JsonProperty("toSemesterId")
    public Long getToSemesterId() { return toSemester != null ? toSemester.getId() : null; }
    @JsonProperty("toSemesterId")
    public void setToSemesterId(Long v) { if (v != null) { Semester s = new Semester(); s.setId(v); this.toSemester = s; } }

    @JsonProperty("fromBatchId")
    public Long getFromBatchId() { return fromBatch != null ? fromBatch.getId() : null; }
    @JsonProperty("fromBatchId")
    public void setFromBatchId(Long v) { if (v != null) { Batch b = new Batch(); b.setId(v); this.fromBatch = b; } }

    @JsonProperty("toBatchId")
    public Long getToBatchId() { return toBatch != null ? toBatch.getId() : null; }
    @JsonProperty("toBatchId")
    public void setToBatchId(Long v) { if (v != null) { Batch b = new Batch(); b.setId(v); this.toBatch = b; } }

    @JsonProperty("approvedById")
    public Long getApprovedById() { return approvedBy != null ? approvedBy.getId() : null; }
    @JsonProperty("approvedById")
    public void setApprovedById(Long v) { if (v != null) { User u = new User(); u.setId(v); this.approvedBy = u; } }
}
