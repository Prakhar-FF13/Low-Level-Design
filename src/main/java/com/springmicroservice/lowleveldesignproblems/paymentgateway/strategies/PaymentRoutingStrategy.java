package com.springmicroservice.lowleveldesignproblems.paymentgateway.strategies;

import java.util.List;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Banks;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentRequest;

public interface PaymentRoutingStrategy {
    Banks selectBank(PaymentRequest paymentRequest, List<Banks> banks);
}
