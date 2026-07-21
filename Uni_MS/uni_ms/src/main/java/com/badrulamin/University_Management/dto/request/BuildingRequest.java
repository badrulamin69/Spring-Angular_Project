package com.badrulamin.University_Management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuildingRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String description;

    private String address;

    private Integer totalFloors;

    private Integer totalRooms;

    private String contactPerson;

    private String contactPhone;

    private Boolean isActive;
}
