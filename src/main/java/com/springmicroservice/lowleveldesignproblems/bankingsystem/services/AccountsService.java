package com.springmicroservice.lowleveldesignproblems.bankingsystem.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.springmicroservice.lowleveldesignproblems.bankingsystem.events.EventPublisher;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.Account;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.EventLog;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.EventType;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.Transaction;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.TransactionType;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountsService {

    private final AccountRepository accountRepository;
    private final EventPublisher eventPublisher;
    private final PaymentService paymentService;

    public boolean createAccount(Integer timestamp, String accountId) {
        int t = timestamp == null ? Integer.MAX_VALUE : timestamp;
        paymentService.processDueCashbacksBefore(t);
        if (accountRepository.exists(accountId)) {
            return false;
        }
        accountRepository.save(new Account(accountId));
        eventPublisher.publish(
            new EventLog(UUID.randomUUID().toString(), t, EventType.ACCOUNT_CREATION));
        return true;
    }

    public Optional<Integer> deposit(Integer timestamp, String accountId, int amount) {
        int t = timestamp == null ? Integer.MAX_VALUE : timestamp;
        paymentService.processDueCashbacksBefore(t);
        if (amount <= 0) {
            return Optional.empty();
        }
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            return Optional.empty();
        }
        Account account = accountOpt.get();
        account.recordDeposit(amount, t, UUID.randomUUID().toString());
        eventPublisher.publish(
            new EventLog(UUID.randomUUID().toString(), t, EventType.DEPOSIT));
        return Optional.of(account.getBalance());
    }

    public Optional<Integer> transfer(
            Integer timestamp, String sourceAccountId, String destinationAccountId, int amount) {
        int t = timestamp == null ? Integer.MAX_VALUE : timestamp;
        paymentService.processDueCashbacksBefore(t);
        if (amount <= 0) {
            return Optional.empty();
        }
        Optional<Account> sourceOpt = accountRepository.findById(sourceAccountId);
        Optional<Account> destOpt = accountRepository.findById(destinationAccountId);
        if (sourceOpt.isEmpty() || destOpt.isEmpty()) {
            return Optional.empty();
        }
        Account source = sourceOpt.get();
        Account destination = destOpt.get();
        String transferTransactionId = UUID.randomUUID().toString();
        if (!source.transferTo(destination, amount, t, transferTransactionId)) {
            return Optional.empty();
        }
        eventPublisher.publish(
            new EventLog(UUID.randomUUID().toString(), t, EventType.TRANSFER));
        return Optional.of(source.getBalance());
    }

    public List<String> topSpenders(Integer timestamp, int n) {
        int cutoff = timestamp == null ? Integer.MAX_VALUE : timestamp;
        paymentService.processDueCashbacksBefore(cutoff);
        if (n <= 0) {
            return List.of();
        }
        record Rank(String accountId, int total) {}
        return accountRepository.findAll().stream()
            .map(a -> new Rank(a.getAccountId(), sumOutgoingUntil(a, cutoff)))
            .sorted(Comparator.comparingInt(Rank::total).reversed().thenComparing(Rank::accountId))
            .limit(n)
            .map(r -> r.accountId() + "(" + r.total() + ")")
            .toList();
    }

    private static int sumOutgoingUntil(Account account, int queryTimeInclusive) {
        return account.getTransactions().stream()
            .filter(t -> t.getTimestamp() <= queryTimeInclusive)
            .filter(
                t ->
                    t.isOutgoing()
                        && (t.getType() == TransactionType.TRANSFER
                            || t.getType() == TransactionType.PAY))
            .mapToInt(Transaction::getAmount)
            .sum();
    }

    public Optional<String> pay(Integer timestamp, String accountId, int amount) {
        return paymentService.pay(timestamp, accountId, amount);
    }

    public Optional<String> getPaymentStatus(Integer timestamp, String accountId, String paymentId) {
        return paymentService.getPaymentStatus(timestamp, accountId, paymentId);
    }

    public boolean mergeAccounts(Integer timestamp, String account1, String account2) {
        if (account1.equals(account2)) {
            return false;
        }
        int t = timestamp == null ? Integer.MAX_VALUE : timestamp;
        paymentService.processDueCashbacksBefore(t);
        Optional<Account> first = accountRepository.findById(account1);
        Optional<Account> second = accountRepository.findById(account2);
        if (first.isEmpty() || second.isEmpty()) {
            return false;
        }
        Account survivor = first.get();
        Account absorbed = second.get();
        survivor.absorbMergedAccount(absorbed);
        accountRepository.save(survivor);
        paymentService.reassignPaymentsAfterMerge(account2, account1);
        accountRepository.delete(account2);
        return true;
    }

    public Optional<Integer> getBalance(Integer timestamp, String accountId, Integer timeAt) {
        int t = timestamp == null ? Integer.MAX_VALUE : timestamp;
        paymentService.processDueCashbacksBefore(t);
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            return Optional.empty();
        }
        int cutoff = timeAt == null ? Integer.MAX_VALUE : timeAt;
        return Optional.of(balanceAtTime(accountOpt.get(), cutoff));
    }

    private static int balanceAtTime(Account account, int timeAtInclusive) {
        List<Transaction> txs = new ArrayList<>(account.getTransactions());
        txs.sort(
            Comparator.comparingInt(Transaction::getTimestamp)
                .thenComparing(Transaction::getTransactionId));
        int balance = 0;
        for (Transaction tx : txs) {
            if (tx.getTimestamp() > timeAtInclusive) {
                break;
            }
            balance += ledgerEffect(tx);
        }
        return balance;
    }

    private static int ledgerEffect(Transaction tx) {
        return switch (tx.getType()) {
            case DEPOSIT, CASHBACK -> tx.getAmount();
            case PAY -> -tx.getAmount();
            case TRANSFER -> tx.isOutgoing() ? -tx.getAmount() : tx.getAmount();
        };
    }
}
