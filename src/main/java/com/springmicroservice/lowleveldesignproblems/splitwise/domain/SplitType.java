package com.springmicroservice.lowleveldesignproblems.splitwise.domain;

/**
 * How an expense total is divided among participants.
 */
public enum SplitType {
    /**
     * Total amount is split evenly across all listed participants (with cent-safe rounding).
     */
    EQUAL,
    /**
     * Each participant owes a percentage of the total; percentages must sum to 100.
     */
    PERCENT
}
