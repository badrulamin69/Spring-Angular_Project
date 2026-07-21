package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "choice_filling_config")
public class ChoiceFillingConfig extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id", nullable = false)
    private AcademicSession session;

    @JsonProperty("sessionId")
    public Long getSessionId() { return session != null ? session.getId() : null; }
    @JsonProperty("sessionId")
    public void setSessionId(Long sessionId) {
        if (sessionId != null) {
            AcademicSession s = new AcademicSession();
            s.setId(sessionId);
            this.session = s;
        }
    }

    @NotNull
    @Column(name = "choice_start_date", nullable = false)
    private LocalDateTime choiceStartDate;

    @NotNull
    @Column(name = "choice_end_date", nullable = false)
    private LocalDateTime choiceEndDate;

    @Column(name = "max_choices", nullable = false)
    private Integer maxChoices = 10;

    @Column(name = "min_choices", nullable = false)
    private Integer minChoices = 1;

    @Column(name = "allow_editing_before_deadline", nullable = false)
    private Boolean allowEditingBeforeDeadline = true;

    @Column(name = "auto_lock_after_deadline", nullable = false)
    private Boolean autoLockAfterDeadline = true;

    @Column(name = "include_waiting_list", nullable = false)
    private Boolean includeWaitingList = false;

    @Column(nullable = false)
    private String status = "DRAFT";

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(nullable = false)
    private boolean active = true;
}
