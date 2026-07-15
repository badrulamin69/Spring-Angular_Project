package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "academic_policies")
public class AcademicPolicy extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "policy_type")
    private String policyType;

    @Column(columnDefinition = "TEXT")
    private String policyValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @JsonProperty("programId")
    public void setProgramId(Long id) {
        if (id != null) {
            this.program = new Program();
            this.program.setId(id);
        }
    }

    @JsonProperty
    public Long getProgramId() {
        return this.program != null ? this.program.getId() : null;
    }

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;
}
