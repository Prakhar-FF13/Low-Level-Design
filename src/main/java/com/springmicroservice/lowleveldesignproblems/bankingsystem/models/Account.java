package com.springmicroservice.lowleveldesignproblems.bankingsystem.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String accountId;
    private int balance;
    private List<Transaction> transactions;

    public Account(String accountId) {
        this.accountId = accountId;
        this.balance = 0;
        this.transactions = new ArrayList<Transaction>();
    }

    public void recordDeposit(int amount, int timestamp, String transactionId) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        this.balance += amount;
        this.transactions.add(
            new Transaction(
                transactionId, TransactionType.DEPOSIT, amount, timestamp, null, false));
    }

    public boolean transferTo(Account destination, int amount, int timestamp, String transactionId) {
        if (amount <= 0 || this.balance < amount || this == destination) {
            return false;
        }
        this.balance -= amount;
        destination.balance += amount;
        this.transactions.add(
            new Transaction(
                transactionId,
                TransactionType.TRANSFER,
                amount,
                timestamp,
                destination.getAccountId(),
                true));
        destination
            .transactions
            .add(
                new Transaction(
                    transactionId,
                    TransactionType.TRANSFER,
                    amount,
                    timestamp,
                    this.accountId,
                    false));
        return true;
    }

    /** Debits for a payment; ledger line is outgoing for ranking. */
    public boolean recordPayment(int amount, int timestamp, String transactionId) {
        if (amount <= 0 || this.balance < amount) {
            return false;
        }
        this.balance -= amount;
        this.transactions.add(
            new Transaction(
                transactionId, TransactionType.PAY, amount, timestamp, null, true));
        return true;
    }

    public void recordCashback(int amount, int timestamp, String transactionId) {
        if (amount <= 0) {
            return;
        }
        this.balance += amount;
        this.transactions.add(
            new Transaction(
                transactionId, TransactionType.CASHBACK, amount, timestamp, null, false));
    }

    /** Adds balance and appends {@code other}'s ledger, sorted by time then transaction id. */
    public void absorbMergedAccount(Account other) {
        if (other == null || this == other) {
            return;
        }
        this.balance += other.balance;
        List<Transaction> merged = new ArrayList<>(this.transactions);
        merged.addAll(other.transactions);
        merged.sort(
            Comparator.comparingInt(Transaction::getTimestamp)
                .thenComparing(Transaction::getTransactionId));
        this.transactions = merged;
    }
}
