package com.springmicroservice.lowleveldesignproblems.paymentgateway.models;

public enum ErrorCode {
    PAYMENT_FAILED,
    BANK_UNAVAILABLE,
    INVALID_PAYMENT_DETAILS,
    INSUFFICIENT_FUNDS,
    TRANSACTION_TIMED_OUT
}
