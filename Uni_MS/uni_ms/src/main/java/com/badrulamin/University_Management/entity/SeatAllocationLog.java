package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seat_allocation_log")
public class SeatAllocationLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allocation_id", nullable = false)
    private DepartmentAllocation allocation;

    @JsonProperty("allocationId")
    public Long getAllocationId() { return allocation != null ? allocation.getId() : null; }
    @JsonProperty("allocationId")
    public void setAllocationId(Long allocationId) {
        if (allocationId != null) {
            DepartmentAllocation a = new DepartmentAllocation();
            a.setId(allocationId);
            this.allocation = a;
        }
    }

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "previous_status", length = 30)
    private String previousStatus;

    @Column(name = "new_status", length = 30)
    private String newStatus;

    @Column(name = "previous_program_id")
    private Long previousProgramId;

    @Column(name = "new_program_id")
    private Long newProgramId;

    @Column(name = "previous_department_id")
    private Long previousDepartmentId;

    @Column(name = "new_department_id")
    private Long newDepartmentId;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @JsonProperty("performedById")
    public Long getPerformedById() { return performedBy != null ? performedBy.getId() : null; }
    @JsonProperty("performedById")
    public void setPerformedById(Long userId) {
        if (userId != null) {
            User u = new User();
            u.setId(userId);
            this.performedBy = u;
        }
    }

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;
}
