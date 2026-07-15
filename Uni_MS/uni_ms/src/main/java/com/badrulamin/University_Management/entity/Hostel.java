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
@Table(name = "hostels")
public class Hostel extends BaseEntity {

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;

    @NotBlank
    @Column(nullable = false)
    private String type;

    private String address;

    @Column(name = "wardens_name")
    private String wardensName;

    @Column(name = "wardens_phone")
    private String wardensPhone;

    @NotNull
    @Column(name = "total_rooms", nullable = false)
    private int totalRooms;
}
