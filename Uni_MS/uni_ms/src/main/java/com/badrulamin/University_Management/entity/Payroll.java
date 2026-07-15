package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payrolls")
public class Payroll extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @Column(name = "pay_period_start", nullable = false)
    private LocalDate payPeriodStart;

    @NotNull
    @Column(name = "pay_period_end", nullable = false)
    private LocalDate payPeriodEnd;

    @NotNull
    @Positive
    @Column(name = "basic_salary", nullable = false)
    private BigDecimal basicSalary;

    private BigDecimal allowances;

    private BigDecimal deductions;

    @NotNull
    @Positive
    @Column(name = "net_salary", nullable = false)
    private BigDecimal netSalary;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    private String status;

    @JsonProperty("employeeId")
    public void setEmployeeId(Long id) {
        if (id != null) {
            this.employee = new Employee();
            this.employee.setId(id);
        }
    }

    @JsonProperty
    public Long getEmployeeId() {
        return this.employee != null ? this.employee.getId() : null;
    }
}
