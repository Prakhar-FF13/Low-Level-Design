package com.springmicroservice.lowleveldesignproblems.paymentgateway.models;

import java.util.List;

import lombok.Data;

@Data
public class Clients {
    private String clientId;
    private String clientName;
    private List<PaymentMethods> paymentMethods;
}
