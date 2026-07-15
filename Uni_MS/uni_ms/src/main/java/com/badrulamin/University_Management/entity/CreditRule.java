package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_rules")
public class CreditRule extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
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

    @NotNull
    @Column(name = "min_credits_per_semester", nullable = false)
    private Integer minCreditsPerSemester = 12;

    @NotNull
    @Column(name = "max_credits_per_semester", nullable = false)
    private Integer maxCreditsPerSemester = 24;

    @NotNull
    @Column(name = "total_required_credits", nullable = false)
    private Integer totalRequiredCredits;

    @Column(name = "max_transfer_credits")
    private Integer maxTransferCredits = 0;

    @Column(name = "max_elective_credits")
    private Integer maxElectiveCredits = 0;

    private String description;
}
