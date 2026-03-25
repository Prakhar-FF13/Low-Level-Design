package com.springmicroservice.lowleveldesignproblems.bankingsystem.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springmicroservice.lowleveldesignproblems.bankingsystem.repository.InMemoryAccountRepository;
import com.springmicroservice.lowleveldesignproblems.bankingsystem.repository.InMemoryPaymentRepository;

class AccountsServiceTest {

    private AccountsService banking;

    @BeforeEach
    void setUp() {
        var accounts = new InMemoryAccountRepository();
        var payments = new InMemoryPaymentRepository();
        var events = new EventManagerService();
        var paymentService = new PaymentService(accounts, payments, events);
        banking = new AccountsService(accounts, events, paymentService);
    }

    @Test
    void mergeAfterDelete_absorbedAccountBalanceHistoryAndPayments() {
        assertTrue(banking.createAccount(100, "A"));
        assertTrue(banking.createAccount(100, "B"));
        assertEquals(1000, banking.deposit(200, "A", 1000).orElseThrow());
        assertEquals(500, banking.deposit(200, "B", 500).orElseThrow());

        assertTrue(banking.mergeAccounts(300, "A", "B"));

        assertEquals(1500, banking.getBalance(400, "A", Integer.MAX_VALUE).orElseThrow());
        assertFalse(banking.getBalance(400, "B", Integer.MAX_VALUE).isPresent());
    }

    @Test
    void merge_reassignsPaymentIdsToSurvivor() {
        assertTrue(banking.createAccount(1000, "A"));
        assertTrue(banking.createAccount(1000, "B"));
        banking.deposit(1100, "B", 500);
        String paymentId =
            banking.pay(1200, "B", 100).orElseThrow();

        assertTrue(banking.mergeAccounts(2000, "A", "B"));

        assertTrue(banking.getPaymentStatus(2000, "A", paymentId).isPresent());
        assertEquals(
            "IN PROGRESS",
            banking.getPaymentStatus(2000, "A", paymentId).orElseThrow());
        assertTrue(banking.getPaymentStatus(2000, "B", paymentId).isEmpty());
    }

    @Test
    void getBalance_replaysMergedHistoryUpToTimeAt() {
        assertTrue(banking.createAccount(100, "A"));
        assertTrue(banking.createAccount(100, "B"));
        assertEquals(100, banking.deposit(100, "A", 100).orElseThrow());
        assertEquals(200, banking.deposit(300, "B", 200).orElseThrow());

        assertTrue(banking.mergeAccounts(500, "A", "B"));

        assertEquals(100, banking.getBalance(500, "A", 200).orElseThrow());
        assertEquals(300, banking.getBalance(500, "A", 400).orElseThrow());
        assertEquals(300, banking.getBalance(500, "A", Integer.MAX_VALUE).orElseThrow());
    }

    @Test
    void getBalance_fullReplayMatchesCurrentBalance() {
        assertTrue(banking.createAccount(10, "A"));
        assertEquals(1000, banking.deposit(20, "A", 1000).orElseThrow());
        assertTrue(banking.pay(30, "A", 200).isPresent());

        assertEquals(800, banking.getBalance(40, "A", Integer.MAX_VALUE).orElseThrow());
    }
}
