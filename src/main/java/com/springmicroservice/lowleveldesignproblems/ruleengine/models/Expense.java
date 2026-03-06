package com.springmicroservice.lowleveldesignproblems.ruleengine.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long tripId;
    private Double amount;
    private ExpenseType expenseType;
    private String description;
    private String vendorName;
}
