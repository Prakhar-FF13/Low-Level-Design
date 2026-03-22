package com.springmicroservice.lowleveldesignproblems.paymentgateway.services;

import java.util.List;
import java.util.UUID;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Clients;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentMethods;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.repository.ClientsRepository;

public class ClientService {
    private final ClientsRepository clientsRepository;

    public ClientService(ClientsRepository clientsRepository) {
        this.clientsRepository = clientsRepository;
    }

    public Clients createClient(String clientName, List<PaymentMethods> paymentMethods) {
        Clients clients = new Clients();
        clients.setClientId(UUID.randomUUID().toString());
        clients.setClientName(clientName);
        clients.setPaymentMethods(paymentMethods);
        return clientsRepository.save(clients);
    }

    public Clients getClientById(String id) {
        return clientsRepository.findById(id).orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public void removeClient(String id) {
        clientsRepository.remove(id);
    }
}
