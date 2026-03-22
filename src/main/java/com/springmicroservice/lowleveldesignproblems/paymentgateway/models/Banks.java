package com.springmicroservice.lowleveldesignproblems.paymentgateway.models;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class Banks {
    private String bankId;
    private String bankName;
    private double failureRate;
    private List<PaymentMethods> supportedPaymentMethods;

    public PaymentResult processPayment(PaymentRequest paymentRequest) {
        if (Math.random() < failureRate) {
            return PaymentResult.failure(ErrorCode.PAYMENT_FAILED, bankId);
        }
        return PaymentResult.success(UUID.randomUUID().toString(), bankId);
    }
}
