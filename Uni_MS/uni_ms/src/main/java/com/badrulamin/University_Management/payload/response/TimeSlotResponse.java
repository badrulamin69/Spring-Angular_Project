package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TimeSlotResponse {
    private Long id;
    private String name;
    private String code;
    private String startTime;
    private String endTime;
    private String slotType;
    private Integer durationMinutes;
    private Integer sortOrder;
    private boolean isActive;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
