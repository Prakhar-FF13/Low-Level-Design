package com.springmicroservice.lowleveldesignproblems.paymentgateway.exceptions;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentMethods;

public class UnsupportedPaymentMethodException extends RuntimeException {
    public UnsupportedPaymentMethodException(String clientId, PaymentMethods method) {
        super("Client " + clientId + " does not support payment method " + method);
    }
}
