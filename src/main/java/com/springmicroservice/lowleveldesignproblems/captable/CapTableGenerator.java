package com.springmicroservice.lowleveldesignproblems.captable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a cap table by applying transactions in chronological order up to an as-of date.
 */
public final class CapTableGenerator {

    private static final int OWNERSHIP_PERCENT_SCALE = 10;
    private static final int VALUE_PER_SHARE_SCALE = 10;
    private static final int STAKEHOLDER_VALUE_SCALE = 2;

    /**
     * Applies all transactions on or before {@code asOfDate} (inclusive), ordered by date then:
     * stock split, share issuance, company valuation.
     *
     * @param transactions input events (may be unsorted; not modified)
     * @param asOfDate     point in time for the snapshot
     * @return one row per stakeholder with a positive share count; names sorted alphabetically
     */
    public List<CapTableModels.StakeholderCapTableRow> generateCapTable(
            List<CapTableModels.Transaction> transactions, LocalDate asOfDate) {

        List<CapTableModels.Transaction> sorted = new ArrayList<>();
        for (CapTableModels.Transaction t : transactions) {
            if (t == null) {
                continue;
            }
            if (!t.getDate().isAfter(asOfDate)) {
                sorted.add(t);
            }
        }

        Collections.sort(
                sorted,
                Comparator.comparing(CapTableModels.Transaction::getDate)
                        .thenComparingInt(CapTableGenerator::typeOrderForSameDay));

        Map<String, Long> sharesByStakeholder = new HashMap<>();
        BigDecimal latestValuation = null;

        for (CapTableModels.Transaction t : sorted) {
            if (t instanceof CapTableModels.StockSplit) {
                applyStockSplit(sharesByStakeholder, (CapTableModels.StockSplit) t);
            } else if (t instanceof CapTableModels.ShareIssuance) {
                applyShareIssuance(sharesByStakeholder, (CapTableModels.ShareIssuance) t);
            } else if (t instanceof CapTableModels.CompanyValuation) {
                latestValuation = applyCompanyValuation(latestValuation, (CapTableModels.CompanyValuation) t);
            }
        }

        long totalOutstanding = sumShares(sharesByStakeholder);
        BigDecimal valuePerShare = null;
        if (latestValuation != null && totalOutstanding > 0) {
            valuePerShare =
                    latestValuation.divide(
                            BigDecimal.valueOf(totalOutstanding),
                            VALUE_PER_SHARE_SCALE,
                            RoundingMode.HALF_UP);
        }

        List<String> names = new ArrayList<>(sharesByStakeholder.keySet());
        Collections.sort(names);

        List<CapTableModels.StakeholderCapTableRow> rows = new ArrayList<>();
        for (String name : names) {
            long shares = sharesByStakeholder.get(name);
            if (shares <= 0) {
                continue;
            }
            BigDecimal ownershipPercent;
            if (totalOutstanding > 0) {
                ownershipPercent =
                        BigDecimal.valueOf(shares)
                                .multiply(BigDecimal.valueOf(100))
                                .divide(
                                        BigDecimal.valueOf(totalOutstanding),
                                        OWNERSHIP_PERCENT_SCALE,
                                        RoundingMode.HALF_UP);
            } else {
                ownershipPercent = BigDecimal.ZERO;
            }

            BigDecimal value = null;
            if (valuePerShare != null) {
                value =
                        BigDecimal.valueOf(shares)
                                .multiply(valuePerShare)
                                .setScale(STAKEHOLDER_VALUE_SCALE, RoundingMode.HALF_UP);
            }

            rows.add(new CapTableModels.StakeholderCapTableRow(name, shares, ownershipPercent, value));
        }

        return rows;
    }

    /**
     * Same calendar day: stock split first, then issuances, then valuation (per README).
     */
    private static int typeOrderForSameDay(CapTableModels.Transaction t) {
        if (t instanceof CapTableModels.StockSplit) {
            return 0;
        }
        if (t instanceof CapTableModels.ShareIssuance) {
            return 1;
        }
        if (t instanceof CapTableModels.CompanyValuation) {
            return 2;
        }
        return 3;
    }

    private static void applyStockSplit(
            Map<String, Long> sharesByStakeholder, CapTableModels.StockSplit split) {
        long factor = split.getFactor();
        if (factor <= 1) {
            return;
        }
        for (Map.Entry<String, Long> entry : sharesByStakeholder.entrySet()) {
            long before = entry.getValue();
            entry.setValue(Math.multiplyExact(before, factor));
        }
    }

    private static void applyShareIssuance(
            Map<String, Long> sharesByStakeholder, CapTableModels.ShareIssuance issuance) {
        long added = issuance.getSharesIssued();
        if (added < 0) {
            return;
        }
        String name = issuance.getStakeholderName();
        if (name == null) {
            return;
        }
        name = name.trim();
        if (name.isEmpty()) {
            return;
        }

        Long current = sharesByStakeholder.get(name);
        long next = current == null ? added : Math.addExact(current, added);
        sharesByStakeholder.put(name, next);
    }

    private static BigDecimal applyCompanyValuation(
            BigDecimal latestValuation, CapTableModels.CompanyValuation valuation) {
        BigDecimal total = valuation.getTotalValuation();
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return latestValuation;
        }
        return total;
    }

    private static long sumShares(Map<String, Long> sharesByStakeholder) {
        long sum = 0;
        for (Long s : sharesByStakeholder.values()) {
            if (s != null) {
                sum = Math.addExact(sum, s);
            }
        }
        return sum;
    }
}
