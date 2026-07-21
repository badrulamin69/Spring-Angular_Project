package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TimeSlotRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Start time is required")
    private String startTime;

    @NotBlank(message = "End time is required")
    private String endTime;

    @NotBlank(message = "Slot type is required")
    private String slotType = "REGULAR";

    @NotNull(message = "Duration minutes is required")
    private Integer durationMinutes;

    private Integer sortOrder = 0;

    private boolean isActive = true;

    private String remarks;
}
