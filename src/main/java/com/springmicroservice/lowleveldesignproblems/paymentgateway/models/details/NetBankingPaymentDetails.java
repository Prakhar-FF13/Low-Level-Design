package com.springmicroservice.lowleveldesignproblems.paymentgateway.models.details;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentMethods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetBankingPaymentDetails implements PaymentDetails {
    private String username;
    private String password;

    @Override
    public PaymentMethods getPaymentMethod() {
        return PaymentMethods.NET_BANKING;
    }

    @Override
    public void validate() {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }
}
