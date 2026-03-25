package com.springmicroservice.lowleveldesignproblems.bankingsystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    public static final int CASHBACK_DELAY_MS = 86_400_000;

    private String paymentId;
    private String accountId;
    /** Debited amount at pay time. */
    private int amount;
    private int payTimestamp;
    /** {@code payTimestamp + CASHBACK_DELAY_MS}. */
    private int cashbackDueTimestamp;
    /** Floor(2% of {@link #amount}). */
    private int cashbackAmount;
    private PaymentStatus status;

    /** 2% rounded down; cashback due {@code payTimestamp + CASHBACK_DELAY_MS}. */
    public static Payment createPending(
            String paymentId, String accountId, int amount, int payTimestamp) {
        int cashback = (amount * 2) / 100;
        int due = payTimestamp + CASHBACK_DELAY_MS;
        return new Payment(
            paymentId,
            accountId,
            amount,
            payTimestamp,
            due,
            cashback,
            PaymentStatus.IN_PROGRESS);
    }
}
