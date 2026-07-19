package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exam_centers")
public class ExamCenter extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false, length = 30)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "total_capacity")
    private Integer totalCapacity;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
