package com.springmicroservice.lowleveldesignproblems.paymentgateway.services;

import java.util.List;
import java.util.UUID;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Banks;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentMethods;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.repository.BankRepository;

public class BankService {
    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public List<Banks> getAllBanks() {
        return bankRepository.findAll();
    }

    public Banks getBankById(String id) {
        return bankRepository.findById(id).orElseThrow(() -> new RuntimeException("Bank not found"));
    }

    public Banks createBank(String bankName, double failureRate, List<PaymentMethods> supportedPaymentMethods) {
        Banks bank = new Banks();
        bank.setBankId(UUID.randomUUID().toString());
        bank.setBankName(bankName);
        bank.setFailureRate(failureRate);
        bank.setSupportedPaymentMethods(supportedPaymentMethods);
        return bankRepository.save(bank);
    }
}
