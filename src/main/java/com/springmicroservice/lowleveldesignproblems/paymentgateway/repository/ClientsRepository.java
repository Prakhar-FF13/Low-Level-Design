package com.springmicroservice.lowleveldesignproblems.paymentgateway.repository;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Clients;

import java.util.Optional;

public interface ClientsRepository {
    Clients save(Clients clients);
    Optional<Clients> findById(String id);
    void remove(String clientId);
}
