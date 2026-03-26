package com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Expense group: a named set of users who share expenses together.
 * <p>
 * {@code creator} is the user who created the group; {@code members} includes everyone who can appear
 * on expenses (creator is typically also in {@code members}).
 */
@Entity
@Table(name = "splitwise_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private UserEntity createdBy;

    @ManyToMany
    @JoinTable(
            name = "splitwise_group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    private Set<UserEntity> members = new HashSet<>();
}
