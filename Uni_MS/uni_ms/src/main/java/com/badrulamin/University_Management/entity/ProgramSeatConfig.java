package com.badrulamin.University_Management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "program_seat_config",
       uniqueConstraints = @UniqueConstraint(columnNames = {"config_id", "program_id"}))
public class ProgramSeatConfig extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "config_id", nullable = false)
    private SeatAllocationConfig config;

    @JsonProperty("configId")
    public Long getConfigId() { return config != null ? config.getId() : null; }
    @JsonProperty("configId")
    public void setConfigId(Long configId) {
        if (configId != null) {
            SeatAllocationConfig c = new SeatAllocationConfig();
            c.setId(configId);
            this.config = c;
        }
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @JsonProperty("facultyId")
    public Long getFacultyId() { return faculty != null ? faculty.getId() : null; }
    @JsonProperty("facultyId")
    public void setFacultyId(Long facultyId) {
        if (facultyId != null) {
            Faculty f = new Faculty();
            f.setId(facultyId);
            this.faculty = f;
        }
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @JsonProperty("departmentId")
    public Long getDepartmentId() { return department != null ? department.getId() : null; }
    @JsonProperty("departmentId")
    public void setDepartmentId(Long departmentId) {
        if (departmentId != null) {
            Department d = new Department();
            d.setId(departmentId);
            this.department = d;
        }
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @JsonProperty("programId")
    public Long getProgramId() { return program != null ? program.getId() : null; }
    @JsonProperty("programId")
    public void setProgramId(Long programId) {
        if (programId != null) {
            Program p = new Program();
            p.setId(programId);
            this.program = p;
        }
    }

    @NotNull
    @Column(name = "shift", nullable = false, length = 20)
    private String shift = "DAY";

    @NotNull
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats = 0;

    @Column(name = "general_seats", nullable = false)
    private Integer generalSeats = 0;

    @Column(name = "quota_seats", nullable = false)
    private Integer quotaSeats = 0;

    @Column(name = "reserved_seats", nullable = false)
    private Integer reservedSeats = 0;

    @Column(name = "allocated_seats", nullable = false)
    private Integer allocatedSeats = 0;

    @Column(name = "waiting_seats", nullable = false)
    private Integer waitingSeats = 0;

    @Column(nullable = false)
    private boolean isActive = true;
}
