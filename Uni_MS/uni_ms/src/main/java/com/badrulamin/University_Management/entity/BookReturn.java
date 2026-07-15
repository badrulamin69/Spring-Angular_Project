package com.badrulamin.University_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_returns")
public class BookReturn extends BaseEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_issue_id", nullable = false)
    private BookIssue bookIssue;

    @NotNull
    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Column(name = "fine_amount", precision = 10, scale = 2)
    private BigDecimal fineAmount;

    @Column(name = "fine_paid")
    private Boolean finePaid = false;

    @Column(name = "condition_at_return")
    private String conditionAtReturn;

    @Column(columnDefinition = "TEXT")
    private String remarks;
}
