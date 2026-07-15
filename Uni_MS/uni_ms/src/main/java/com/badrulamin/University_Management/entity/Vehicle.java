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
@Table(name = "vehicles")
public class Vehicle extends BaseEntity {

    @NotBlank
    @Column(name = "vehicle_number", unique = true, nullable = false)
    private String vehicleNumber;

    @NotBlank
    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    @NotNull
    @Column(nullable = false)
    private int capacity;

    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "driver_phone")
    private String driverPhone;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
