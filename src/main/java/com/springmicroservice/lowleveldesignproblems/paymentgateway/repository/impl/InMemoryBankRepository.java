package com.springmicroservice.lowleveldesignproblems.paymentgateway.repository.impl;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.repository.BankRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Banks;

public class InMemoryBankRepository implements BankRepository {
    private final Map<String, Banks> banksById = new ConcurrentHashMap<>();

    @Override
    public Banks save(Banks bank) {
        banksById.put(bank.getBankId(), bank);
        return bank;
    }

    @Override
    public Optional<Banks> findById(String id) {
        return Optional.ofNullable(banksById.get(id));
    }

    @Override
    public List<Banks> findAll() {
        return new ArrayList<>(banksById.values());
    }
}
