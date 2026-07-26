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
@Table(name = "campuses")
public class Campus extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;

    private String address;

    private String phone;

    @Column(unique = true)
    private String email;

    private String campusType;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private boolean isActive = true;
}
