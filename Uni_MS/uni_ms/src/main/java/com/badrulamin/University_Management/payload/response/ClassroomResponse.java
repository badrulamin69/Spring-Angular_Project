package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClassroomResponse {
    private Long id;
    private Long buildingId;
    private String buildingName;
    private String buildingCode;
    private String roomNumber;
    private Integer floor;
    private Integer capacity;
    private String roomType;
    private boolean isLab;
    private boolean isSmartClassroom;
    private boolean hasProjector;
    private boolean hasWhiteboard;
    private boolean hasWifi;
    private String equipment;
    private boolean isAvailable;
    private boolean isActive;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
