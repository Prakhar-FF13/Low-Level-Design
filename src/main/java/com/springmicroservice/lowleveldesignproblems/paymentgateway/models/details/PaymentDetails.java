package com.springmicroservice.lowleveldesignproblems.paymentgateway.models.details;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentMethods;

public interface PaymentDetails {
    PaymentMethods getPaymentMethod();
    void validate();
}
