package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassroomRequest {
    @NotNull(message = "Building ID is required")
    private Long buildingId;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    private Integer floor = 0;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity = 40;

    @NotBlank(message = "Room type is required")
    private String roomType = "LECTURE_HALL";

    private boolean isLab = false;

    private boolean isSmartClassroom = false;

    private boolean hasProjector = false;

    private boolean hasWhiteboard = true;

    private boolean hasWifi = true;

    private String equipment;

    private boolean isAvailable = true;

    private boolean isActive = true;

    private String remarks;
}
