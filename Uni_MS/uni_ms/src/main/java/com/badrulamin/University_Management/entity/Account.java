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
@Table(name = "accounts")
public class Account extends BaseEntity {

    @NotBlank
    @Column(name = "account_name", nullable = false)
    private String accountName;

    @NotBlank
    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @NotBlank
    @Column(name = "account_type", nullable = false)
    private String accountType;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance;

    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
