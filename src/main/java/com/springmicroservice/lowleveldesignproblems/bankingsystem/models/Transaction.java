package com.springmicroservice.lowleveldesignproblems.bankingsystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String transactionId;
    private TransactionType type;
    private int amount;
    private int timestamp;
    /** For {@link TransactionType#TRANSFER}, the other account; {@code null} for {@link TransactionType#DEPOSIT}. */
    private String peerAccountId;
    /**
     * {@code true} when this account was the sender in a transfer (counts toward outgoing / top spenders).
     * {@code false} for deposits and for the receiving leg of a transfer.
     */
    private boolean outgoing;
}
