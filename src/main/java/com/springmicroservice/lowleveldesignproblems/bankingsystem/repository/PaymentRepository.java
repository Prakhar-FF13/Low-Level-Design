package com.springmicroservice.lowleveldesignproblems.bankingsystem.repository;

import java.util.List;
import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.Payment;

public interface PaymentRepository {

    void save(Payment payment);

    Optional<Payment> findById(String paymentId);

    default Optional<Payment> findByAccountAndPaymentId(String accountId, String paymentId) {
        return findById(paymentId).filter(p -> accountId.equals(p.getAccountId()));
    }

    List<Payment> findAll();

    void reassignAccountId(String fromAccountId, String toAccountId);
}
