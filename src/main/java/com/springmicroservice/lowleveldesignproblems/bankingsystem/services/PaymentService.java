package com.springmicroservice.lowleveldesignproblems.bankingsystem.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.springmicroservice.lowleveldesignproblems.bankingsystem.events.EventPublisher;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.Account;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.EventLog;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.EventType;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.Payment;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.PaymentStatus;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.repository.AccountRepository;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentService {

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final EventPublisher eventPublisher;

    public void reassignPaymentsAfterMerge(String fromAccountId, String toAccountId) {
        paymentRepository.reassignAccountId(fromAccountId, toAccountId);
    }

    public void processDueCashbacksBefore(int operationTimestamp) {
        List<Payment> due =
            paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.IN_PROGRESS)
                .filter(p -> p.getCashbackDueTimestamp() <= operationTimestamp)
                .sorted(
                    Comparator.comparingInt(Payment::getCashbackDueTimestamp)
                        .thenComparing(Payment::getPaymentId))
                .toList();
        for (Payment p : due) {
            applyCashback(p);
        }
    }

    private void applyCashback(Payment payment) {
        Optional<Account> opt = accountRepository.findById(payment.getAccountId());
        if (opt.isEmpty()) {
            return;
        }
        Account account = opt.get();
        int cashback = payment.getCashbackAmount();
        int dueTs = payment.getCashbackDueTimestamp();
        if (cashback > 0) {
            account.recordCashback(cashback, dueTs, UUID.randomUUID().toString());
            accountRepository.save(account);
        }
        payment.setStatus(PaymentStatus.CASHBACK_RECEIVED);
        paymentRepository.save(payment);
        eventPublisher.publish(new EventLog(UUID.randomUUID().toString(), dueTs, EventType.CASHBACK));
    }

    public Optional<String> pay(Integer timestamp, String accountId, int amount) {
        if (amount <= 0) {
            return Optional.empty();
        }
        int t = timestamp == null ? Integer.MAX_VALUE : timestamp;
        processDueCashbacksBefore(t);
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            return Optional.empty();
        }
        Account account = accountOpt.get();
        String paymentId = UUID.randomUUID().toString();
        String ledgerTransactionId = UUID.randomUUID().toString();
        if (!account.recordPayment(amount, t, ledgerTransactionId)) {
            return Optional.empty();
        }
        accountRepository.save(account);
        Payment pending = Payment.createPending(paymentId, accountId, amount, t);
        paymentRepository.save(pending);
        eventPublisher.publish(
            new EventLog(UUID.randomUUID().toString(), t, EventType.PAYMENT));
        return Optional.of(paymentId);
    }

    public Optional<String> getPaymentStatus(Integer timestamp, String accountId, String paymentId) {
        int t = timestamp == null ? Integer.MAX_VALUE : timestamp;
        processDueCashbacksBefore(t);
        return paymentRepository
            .findByAccountAndPaymentId(accountId, paymentId)
            .filter(p -> timestamp == null || timestamp >= p.getPayTimestamp())
            .map(p -> p.getStatus().toApiString());
    }
}
