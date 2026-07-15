package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "semesters")
public class Semester extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_session_id", nullable = false)
    private AcademicSession academicSession;

    @JsonProperty("academicSessionId")
    public void setAcademicSessionId(Long id) {
        if (id != null) {
            this.academicSession = new AcademicSession();
            this.academicSession.setId(id);
        }
    }

    @JsonProperty
    public Long getAcademicSessionId() {
        return this.academicSession != null ? this.academicSession.getId() : null;
    }

    private Integer orderNo;

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate endDate;

    private LocalDate registrationDeadline;

    private String status;

    @Column(nullable = false)
    private boolean isActive = true;
}
