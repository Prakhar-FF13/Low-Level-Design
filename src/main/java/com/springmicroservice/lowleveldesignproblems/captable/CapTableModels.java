package com.springmicroservice.lowleveldesignproblems.captable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Domain types for cap table transactions and output rows.
 */
public final class CapTableModels {

    private CapTableModels() {}

    /** Common contract: every transaction has a date for sorting and filtering. */
    public interface Transaction {
        LocalDate getDate();
    }

    /** Issues shares to a stakeholder (in current share units at issuance time). */
    public static final class ShareIssuance implements Transaction {
        private final LocalDate date;
        private final String stakeholderName;
        private final long sharesIssued;

        public ShareIssuance(LocalDate date, String stakeholderName, long sharesIssued) {
            this.date = date;
            this.stakeholderName = stakeholderName;
            this.sharesIssued = sharesIssued;
        }

        @Override
        public LocalDate getDate() {
            return date;
        }

        public String getStakeholderName() {
            return stakeholderName;
        }

        public long getSharesIssued() {
            return sharesIssued;
        }
    }

    /** Multiplies every outstanding share by {@code factor}. */
    public static final class StockSplit implements Transaction {
        private final LocalDate date;
        private final long factor;

        public StockSplit(LocalDate date, long factor) {
            this.date = date;
            this.factor = factor;
        }

        @Override
        public LocalDate getDate() {
            return date;
        }

        public long getFactor() {
            return factor;
        }
    }

    /** Total company valuation at a point in time. */
    public static final class CompanyValuation implements Transaction {
        private final LocalDate date;
        private final BigDecimal totalValuation;

        public CompanyValuation(LocalDate date, BigDecimal totalValuation) {
            this.date = date;
            this.totalValuation = totalValuation;
        }

        @Override
        public LocalDate getDate() {
            return date;
        }

        public BigDecimal getTotalValuation() {
            return totalValuation;
        }
    }

    /** One row in the cap table as of a given date. */
    public static final class StakeholderCapTableRow {
        private final String name;
        private final long shares;
        private final BigDecimal ownershipPercent;
        private final BigDecimal value;

        public StakeholderCapTableRow(
                String name, long shares, BigDecimal ownershipPercent, BigDecimal value) {
            this.name = name;
            this.shares = shares;
            this.ownershipPercent = ownershipPercent;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public long getShares() {
            return shares;
        }

        public BigDecimal getOwnershipPercent() {
            return ownershipPercent;
        }

        /** May be {@code null} when no valuation applies. */
        public BigDecimal getValue() {
            return value;
        }
    }
}
