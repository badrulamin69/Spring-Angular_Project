package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registration_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @JsonProperty("semesterId")
    public void setSemesterId(Long id) {
        if (id != null) {
            this.semester = new Semester();
            this.semester.setId(id);
        }
    }

    @JsonProperty
    public Long getSemesterId() {
        return this.semester != null ? this.semester.getId() : null;
    }

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(name = "min_credits", nullable = false)
    private Integer minCredits = 12;

    @NotNull
    @Column(name = "max_credits", nullable = false)
    private Integer maxCredits = 24;

    @Column(name = "allow_add_drop", nullable = false)
    private Boolean allowAddDrop = true;

    @Column(name = "add_drop_deadline")
    private LocalDate addDropDeadline;

    @Column(name = "advisor_approval_required", nullable = false)
    private Boolean advisorApprovalRequired = true;

    @Column(name = "payment_required", nullable = false)
    private Boolean paymentRequired = true;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isClosed = false;

    @Column(length = 500)
    private String remarks;

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
