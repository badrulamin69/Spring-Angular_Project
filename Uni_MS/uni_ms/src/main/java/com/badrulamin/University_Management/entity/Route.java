package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "routes")
public class Route extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(name = "route_code", unique = true, nullable = false)
    private String routeCode;

    @NotBlank
    @Column(name = "start_point", nullable = false)
    private String startPoint;

    @NotBlank
    @Column(name = "end_point", nullable = false)
    private String endPoint;

    @Column(name = "distance_km", precision = 10, scale = 2)
    private BigDecimal distanceKm;

    @Column(precision = 10, scale = 2)
    private BigDecimal fare;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
