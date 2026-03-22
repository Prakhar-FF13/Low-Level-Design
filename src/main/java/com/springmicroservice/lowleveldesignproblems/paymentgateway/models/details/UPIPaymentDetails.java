package com.springmicroservice.lowleveldesignproblems.paymentgateway.models.details;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentMethods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UPIPaymentDetails implements PaymentDetails {
    private String vpaId;

    @Override
    public PaymentMethods getPaymentMethod() {
        return PaymentMethods.UPI;
    }

    @Override
    public void validate() {
        if (vpaId == null || vpaId.isBlank()) {
            throw new IllegalArgumentException("VPA ID cannot be null or empty");
        }
        if (!vpaId.contains("@")) {
            throw new IllegalArgumentException("VPA ID must be in format user@bank");
        }
    }
}
