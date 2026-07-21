package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "buildings")
public class Building extends BaseEntity {

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;

    private String description;

    private String address;

    @Column(nullable = false)
    private Integer totalFloors = 1;

    @Column(nullable = false)
    private Integer totalRooms = 0;

    private String contactPerson;

    private String contactPhone;

    @Column(nullable = false)
    private boolean isActive = true;
}
