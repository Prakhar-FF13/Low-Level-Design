package com.springmicroservice.lowleveldesignproblems.bankingsystem.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.Account;

public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> accountsById = new ConcurrentHashMap<>();

    @Override
    public boolean exists(String accountId) {
        return accountsById.containsKey(accountId);
    }

    @Override
    public Optional<Account> findById(String accountId) {
        return Optional.ofNullable(accountsById.get(accountId));
    }

    @Override
    public void save(Account account) {
        accountsById.put(account.getAccountId(), account);
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(accountsById.values());
    }

    @Override
    public void delete(String accountId) {
        accountsById.remove(accountId);
    }
}
