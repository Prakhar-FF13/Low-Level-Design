package com.springmicroservice.lowleveldesignproblems.paymentgateway.strategies;

import java.util.List;
import java.util.Map;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Banks;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentMethods;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentRequest;

/**
 * Routes payments by payment mode: e.g., all CREDIT_CARD → HDFC, all UPI → ICICI.
 * Configuration: Map of PaymentMethod → bankId.
 */
public class PaymentModeRoutingStrategy implements PaymentRoutingStrategy {
    private final Map<PaymentMethods, String> paymentMethodToBankId;

    public PaymentModeRoutingStrategy(Map<PaymentMethods, String> paymentMethodToBankId) {
        this.paymentMethodToBankId = Map.copyOf(paymentMethodToBankId);
    }

    @Override
    public Banks selectBank(PaymentRequest paymentRequest, List<Banks> banks) {
        String targetBankId = paymentMethodToBankId.get(paymentRequest.getPaymentMethod());
        if (targetBankId == null) {
            return null;
        }
        return banks.stream()
                .filter(b -> targetBankId.equals(b.getBankId()))
                .filter(b -> b.getSupportedPaymentMethods() != null
                        && b.getSupportedPaymentMethods().contains(paymentRequest.getPaymentMethod()))
                .findFirst()
                .orElse(null);
    }
}
