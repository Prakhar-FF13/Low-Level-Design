package com.springmicroservice.lowleveldesignproblems.paymentgateway.strategies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Banks;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentRequest;

/**
 * Routes payments to banks based on configurable weights (e.g., 30% bank1, 70% bank2).
 * Weights are specified as bankId -> weight; they are normalized among eligible banks.
 */
public class WeightedRoutingStrategy implements PaymentRoutingStrategy {
    private final Map<String, Double> bankWeights;

    public WeightedRoutingStrategy(Map<String, Double> bankWeights) {
        this.bankWeights = new HashMap<>(bankWeights);
    }

    @Override
    public Banks selectBank(PaymentRequest paymentRequest, List<Banks> banks) {
        List<Banks> eligible = banks.stream()
                .filter(b -> b.getSupportedPaymentMethods() != null
                        && b.getSupportedPaymentMethods().contains(paymentRequest.getPaymentMethod()))
                .filter(b -> bankWeights.containsKey(b.getBankId()) && bankWeights.get(b.getBankId()) > 0)
                .collect(Collectors.toList());

        if (eligible.isEmpty()) {
            return null;
        }
        if (eligible.size() == 1) {
            return eligible.get(0);
        }

        double totalWeight = eligible.stream()
                .mapToDouble(b -> bankWeights.get(b.getBankId()))
                .sum();

        if (totalWeight <= 0) {
            return eligible.get(0);
        }

        double r = Math.random();
        double cumulative = 0;
        for (Banks bank : eligible) {
            cumulative += bankWeights.get(bank.getBankId()) / totalWeight;
            if (r < cumulative) {
                return bank;
            }
        }
        return eligible.get(eligible.size() - 1);
    }
}
