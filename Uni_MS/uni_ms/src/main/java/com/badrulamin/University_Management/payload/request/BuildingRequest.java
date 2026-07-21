package com.badrulamin.University_Management.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BuildingRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Code is required")
    private String code;

    private String description;

    private String address;

    private Integer totalFloors = 1;

    private Integer totalRooms = 0;

    private String contactPerson;

    private String contactPhone;

    private boolean isActive = true;
}
