package com.springmicroservice.lowleveldesignproblems.paymentgateway.repository.impl;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.repository.ClientsRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Clients;

public class InMemoryClientsRepository implements ClientsRepository {
    private final Map<String, Clients> clientsById = new ConcurrentHashMap<>();

    @Override
    public Clients save(Clients clients) {
        clientsById.put(clients.getClientId(), clients);
        return clients;
    }

    @Override
    public Optional<Clients> findById(String id) {
        return Optional.ofNullable(clientsById.get(id));
    }

    @Override
    public void remove(String clientId) {
        clientsById.remove(clientId);
    }
}
