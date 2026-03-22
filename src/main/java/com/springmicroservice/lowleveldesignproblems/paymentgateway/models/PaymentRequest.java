package com.springmicroservice.lowleveldesignproblems.paymentgateway.models;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.details.PaymentDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String clientId;
    private double amount;
    private PaymentMethods paymentMethod;
    private PaymentDetails paymentDetails;
}
