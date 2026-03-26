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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One participant's share of an expense.
 * <ul>
 *   <li><b>EQUAL</b>: {@code percent} is null; {@code owedAmount} is the equal share.</li>
 *   <li><b>PERCENT</b>: {@code percent} is set (0–100); {@code owedAmount} is the monetary share.</li>
 * </ul>
 */
@Entity
@Table(name = "splitwise_expense_split")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSplitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "expense_id")
    private ExpenseEntity expense;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /**
     * For PERCENT splits: percentage of total (sum across splits must be 100).
     */
    @Column(precision = 9, scale = 4)
    private BigDecimal percent;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal owedAmount;
}
