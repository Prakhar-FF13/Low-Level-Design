package com.springmicroservice.lowleveldesignproblems.paymentgateway.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResult {
    private boolean success;
    private String transactionId;
    private ErrorCode errorCode;
    private String bankId;

    public static PaymentResult success(String transactionId, String bankId) {
        return PaymentResult.builder()
                .success(true)
                .transactionId(transactionId)
                .bankId(bankId)
                .build();
    }

    public static PaymentResult failure(ErrorCode errorCode, String bankId) {
        return PaymentResult.builder()
                .success(false)
                .errorCode(errorCode)
                .bankId(bankId)
                .build();
    }
}
