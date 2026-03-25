package com.springmicroservice.lowleveldesignproblems.bankingsystem.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.Payment;

public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<String, Payment> paymentsById = new ConcurrentHashMap<>();

    @Override
    public void save(Payment payment) {
        paymentsById.put(payment.getPaymentId(), payment);
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(paymentsById.get(paymentId));
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(paymentsById.values());
    }

    @Override
    public void reassignAccountId(String fromAccountId, String toAccountId) {
        for (Payment p : paymentsById.values()) {
            if (fromAccountId.equals(p.getAccountId())) {
                p.setAccountId(toAccountId);
                paymentsById.put(p.getPaymentId(), p);
            }
        }
    }
}
