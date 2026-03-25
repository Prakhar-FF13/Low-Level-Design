package com.springmicroservice.lowleveldesignproblems.bankingsystem.models;

public enum PaymentStatus {
    IN_PROGRESS,
    CASHBACK_RECEIVED;

    public String toApiString() {
        return switch (this) {
            case IN_PROGRESS -> "IN PROGRESS";
            case CASHBACK_RECEIVED -> "CASHBACK RECEIVED";
        };
    }
}
