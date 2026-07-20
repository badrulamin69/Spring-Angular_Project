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
@Table(name = "seat_allocation_config")
public class SeatAllocationConfig extends BaseEntity {

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
    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "allocation_round", nullable = false)
    private Integer allocationRound = 1;

    @Column(name = "auto_allocation", nullable = false)
    private Boolean autoAllocation = true;

    @Column(name = "manual_allocation", nullable = false)
    private Boolean manualAllocation = true;

    @NotNull
    @Column(name = "allocation_start_date", nullable = false)
    private LocalDateTime allocationStartDate;

    @NotNull
    @Column(name = "allocation_end_date", nullable = false)
    private LocalDateTime allocationEndDate;

    @Column(name = "accept_deadline_hours", nullable = false)
    private Integer acceptDeadlineHours = 72;

    @Column(name = "lock_after_publish", nullable = false)
    private Boolean lockAfterPublish = true;

    @Column(name = "enable_quota", nullable = false)
    private Boolean enableQuota = false;

    @Column(name = "enable_reserved_seats", nullable = false)
    private Boolean enableReservedSeats = false;

    @Column(nullable = false, length = 30)
    private String status = "DRAFT";

    @Column(name = "remarks", length = 500)
    private String remarks;
}
