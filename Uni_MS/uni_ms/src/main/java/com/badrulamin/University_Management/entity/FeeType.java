package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fee_types")
public class FeeType extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String code;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String category;

    private String description;

    @Column(name = "default_amount")
    private Double defaultAmount;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
