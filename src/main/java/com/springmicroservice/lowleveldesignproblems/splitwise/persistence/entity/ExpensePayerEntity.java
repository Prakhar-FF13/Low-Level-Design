package com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One person who put money toward this expense. Several rows may exist per expense (multi-payer).
 * The sum of {@code paidAmount} across all payers for an expense must equal the expense total
 * (enforced when saving in the service layer).
 */
@Entity
@Table(
        name = "splitwise_expense_payer",
        uniqueConstraints = @UniqueConstraint(columnNames = {"expense_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpensePayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "expense_id")
    private ExpenseEntity expense;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal paidAmount;
}
