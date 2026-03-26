package com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.springmicroservice.lowleveldesignproblems.splitwise.domain.SplitType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A single expense in a group: who paid how much (see {@link ExpensePayerEntity}) and who owes how
 * much (see {@link ExpenseSplitEntity}).
 */
@Entity
@Table(name = "splitwise_expense")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SplitType splitType;

    @Column(nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExpensePayerEntity> payers = new ArrayList<>();

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExpenseSplitEntity> splits = new ArrayList<>();
}
