package com.badrulamin.University_Management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
