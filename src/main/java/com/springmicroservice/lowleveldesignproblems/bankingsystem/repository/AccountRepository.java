package com.springmicroservice.lowleveldesignproblems.bankingsystem.repository;

import java.util.List;
import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.bankingsystem.models.Account;

/** Account persistence port (DIP): hide storage behind an interface. */
public interface AccountRepository {

    boolean exists(String accountId);

    Optional<Account> findById(String accountId);

    void save(Account account);

    List<Account> findAll();

    void delete(String accountId);
}
