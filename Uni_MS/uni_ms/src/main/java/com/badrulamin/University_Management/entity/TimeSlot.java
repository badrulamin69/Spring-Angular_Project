package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "time_slots")
public class TimeSlot extends BaseEntity {

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;

    @NotBlank
    @Column(nullable = false, length = 5)
    private String startTime;

    @NotBlank
    @Column(nullable = false, length = 5)
    private String endTime;

    @NotBlank
    @Column(nullable = false)
    private String slotType = "REGULAR";

    @NotNull
    @Column(nullable = false)
    private Integer durationMinutes;

    @NotNull
    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private boolean isActive = true;

    private String remarks;
}
