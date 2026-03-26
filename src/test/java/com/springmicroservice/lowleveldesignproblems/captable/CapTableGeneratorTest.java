package com.springmicroservice.lowleveldesignproblems.captable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CapTableGeneratorTest {

    private final CapTableGenerator generator = new CapTableGenerator();

    @Test
    void walkthrough_issuanceSplitIssuanceValuation() {
        LocalDate asOf = LocalDate.of(2020, 6, 1);
        List<CapTableModels.Transaction> txs = new ArrayList<>();
        txs.add(new CapTableModels.ShareIssuance(LocalDate.of(2020, 1, 1), "Alice", 1000));
        txs.add(new CapTableModels.ShareIssuance(LocalDate.of(2020, 2, 1), "Bob", 500));
        txs.add(new CapTableModels.StockSplit(LocalDate.of(2020, 3, 1), 2));
        txs.add(new CapTableModels.ShareIssuance(LocalDate.of(2020, 4, 1), "Alice", 100));
        txs.add(
                new CapTableModels.CompanyValuation(
                        LocalDate.of(2020, 5, 1), new BigDecimal("10000000")));

        List<CapTableModels.StakeholderCapTableRow> rows = generator.generateCapTable(txs, asOf);
        assertEquals(2, rows.size());

        CapTableModels.StakeholderCapTableRow alice = findByName(rows, "Alice");
        CapTableModels.StakeholderCapTableRow bob = findByName(rows, "Bob");
        assertEquals(2100L, alice.getShares());
        assertEquals(1000L, bob.getShares());

        assertEquals(
                new BigDecimal("67.7419354839"),
                alice.getOwnershipPercent().setScale(10, java.math.RoundingMode.HALF_UP));
        assertEquals(
                new BigDecimal("32.2580645161"),
                bob.getOwnershipPercent().setScale(10, java.math.RoundingMode.HALF_UP));

        assertEquals(new BigDecimal("6774193.55"), alice.getValue());
        assertEquals(new BigDecimal("3225806.45"), bob.getValue());
    }

    @Test
    void noValuation_valueIsNullButSharesAndPercentComputed() {
        LocalDate asOf = LocalDate.of(2020, 4, 15);
        List<CapTableModels.Transaction> txs = new ArrayList<>();
        txs.add(new CapTableModels.ShareIssuance(LocalDate.of(2020, 1, 1), "Alice", 1000));
        txs.add(new CapTableModels.ShareIssuance(LocalDate.of(2020, 2, 1), "Bob", 500));

        List<CapTableModels.StakeholderCapTableRow> rows = generator.generateCapTable(txs, asOf);
        assertEquals(2, rows.size());
        assertNull(findByName(rows, "Alice").getValue());
        assertNull(findByName(rows, "Bob").getValue());
        assertEquals(
                new BigDecimal("66.6666666667"),
                findByName(rows, "Alice")
                        .getOwnershipPercent()
                        .setScale(10, java.math.RoundingMode.HALF_UP));
    }

    @Test
    void sameDate_orderIsSplitThenIssuanceThenValuation() {
        LocalDate d = LocalDate.of(2020, 1, 1);
        List<CapTableModels.Transaction> txs = new ArrayList<>();
        txs.add(new CapTableModels.ShareIssuance(d, "Alice", 50));
        txs.add(new CapTableModels.StockSplit(d, 2));

        List<CapTableModels.StakeholderCapTableRow> rows = generator.generateCapTable(txs, d);
        assertEquals(1, rows.size());
        assertEquals(50L, rows.get(0).getShares());
    }

    @Test
    void asOfDate_ignoresFutureTransactions() {
        LocalDate asOf = LocalDate.of(2020, 3, 1);
        List<CapTableModels.Transaction> txs = new ArrayList<>();
        txs.add(new CapTableModels.ShareIssuance(LocalDate.of(2020, 1, 1), "Alice", 100));
        txs.add(new CapTableModels.ShareIssuance(LocalDate.of(2020, 5, 1), "Bob", 900));

        List<CapTableModels.StakeholderCapTableRow> rows = generator.generateCapTable(txs, asOf);
        assertEquals(1, rows.size());
        assertEquals("Alice", rows.get(0).getName());
        assertEquals(100L, rows.get(0).getShares());
    }

    @Test
    void invalidInputs_ignored() {
        LocalDate d = LocalDate.of(2020, 1, 1);
        List<CapTableModels.Transaction> txs = new ArrayList<>();
        txs.add(new CapTableModels.StockSplit(d, 1));
        txs.add(new CapTableModels.StockSplit(d, 0));
        txs.add(new CapTableModels.ShareIssuance(d, "Alice", -10));
        txs.add(new CapTableModels.ShareIssuance(d, "Alice", 100));
        txs.add(new CapTableModels.CompanyValuation(d, BigDecimal.ZERO));
        txs.add(new CapTableModels.CompanyValuation(d, new BigDecimal("-1")));

        List<CapTableModels.StakeholderCapTableRow> rows = generator.generateCapTable(txs, d);
        assertEquals(1, rows.size());
        assertEquals(100L, rows.get(0).getShares());
        assertNull(rows.get(0).getValue());
    }

    @Test
    void emptyTransactionList_returnsEmptyRows() {
        assertTrue(generator.generateCapTable(new ArrayList<>(), LocalDate.of(2020, 1, 1)).isEmpty());
    }

    @Test
    void nullTransactions_skipped() {
        LocalDate d = LocalDate.of(2020, 1, 1);
        List<CapTableModels.Transaction> txs = new ArrayList<>();
        txs.add(null);
        txs.add(new CapTableModels.ShareIssuance(d, "Alice", 10));

        List<CapTableModels.StakeholderCapTableRow> rows = generator.generateCapTable(txs, d);
        assertEquals(1, rows.size());
        assertEquals(10L, rows.get(0).getShares());
    }

    private static CapTableModels.StakeholderCapTableRow findByName(
            List<CapTableModels.StakeholderCapTableRow> rows, String name) {
        for (CapTableModels.StakeholderCapTableRow row : rows) {
            if (name.equals(row.getName())) {
                return row;
            }
        }
        throw new AssertionError("No row for " + name);
    }
}
