package com.springmicroservice.lowleveldesignproblems.bankingsystem.models;

public enum TransactionType {
    /** Cash in; no peer account. */
    DEPOSIT,
    /** Move between two accounts; {@link Transaction#getPeerAccountId()} is the other side. */
    TRANSFER,
    /** Purchase / debit; {@link Transaction#getPeerAccountId()} is null; counts as outgoing for top spenders. */
    PAY,
    /** Cashback credit; not outgoing. */
    CASHBACK
}
