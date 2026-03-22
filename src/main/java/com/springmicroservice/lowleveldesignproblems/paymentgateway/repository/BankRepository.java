package com.springmicroservice.lowleveldesignproblems.paymentgateway.repository;

import java.util.List;
import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Banks;

public interface BankRepository {
    Banks save(Banks bank);
    Optional<Banks> findById(String id);
    List<Banks> findAll();
}
