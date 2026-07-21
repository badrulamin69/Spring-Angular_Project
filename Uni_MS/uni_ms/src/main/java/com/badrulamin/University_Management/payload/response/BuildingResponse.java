package com.badrulamin.University_Management.payload.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BuildingResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String address;
    private Integer totalFloors;
    private Integer totalRooms;
    private String contactPerson;
    private String contactPhone;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
