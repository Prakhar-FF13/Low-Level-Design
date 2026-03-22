package com.springmicroservice.lowleveldesignproblems.paymentgateway;

import java.util.List;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.exceptions.UnsupportedPaymentMethodException;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Banks;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Clients;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.ErrorCode;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentMethods;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentRequest;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentResult;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.details.PaymentDetails;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.services.BankService;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.services.ClientService;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.services.TrafficLogger;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.strategies.PaymentRoutingStrategy;

/**
 * Facade for the Payment Gateway. Orchestrates client onboarding and payment capture.
 */
public class PaymentGatewayOrchestrator {
    private final ClientService clientService;
    private final BankService bankService;
    private final PaymentRoutingStrategy paymentRoutingStrategy;
    private final TrafficLogger trafficLogger;

    public PaymentGatewayOrchestrator(ClientService clientService, BankService bankService,
                                    PaymentRoutingStrategy paymentRoutingStrategy) {
        this(clientService, bankService, paymentRoutingStrategy, new TrafficLogger());
    }

    public PaymentGatewayOrchestrator(ClientService clientService, BankService bankService,
                                    PaymentRoutingStrategy paymentRoutingStrategy, TrafficLogger trafficLogger) {
        this.clientService = clientService;
        this.bankService = bankService;
        this.paymentRoutingStrategy = paymentRoutingStrategy;
        this.trafficLogger = trafficLogger;
    }

    /**
     * Onboard a new client with supported payment methods.
     *
     * @return clientId of the created client
     */
    public String onboardClient(String clientName, List<PaymentMethods> supportedMethods) {
        if (supportedMethods == null || supportedMethods.isEmpty()) {
            throw new IllegalArgumentException("Supported payment methods cannot be empty");
        }
        Clients client = clientService.createClient(clientName, supportedMethods);
        return client.getClientId();
    }

    /**
     * Remove a client from the payment gateway.
     */
    public void removeClient(String clientId) {
        clientService.removeClient(clientId);
    }

    /**
     * Capture a payment: validate client, validate details, route to bank, process.
     */
    public PaymentResult capturePayment(String clientId, double amount, PaymentMethods paymentMethod, PaymentDetails paymentDetails) {
        if (paymentDetails == null) {
            return logAndReturn(clientId, null, PaymentResult.failure(ErrorCode.INVALID_PAYMENT_DETAILS, null));
        }
        if (paymentMethod != paymentDetails.getPaymentMethod()) {
            return logAndReturn(clientId, null, PaymentResult.failure(ErrorCode.INVALID_PAYMENT_DETAILS, null));
        }

        Clients client = clientService.getClientById(clientId);
        if (client.getPaymentMethods() == null || !client.getPaymentMethods().contains(paymentMethod)) {
            throw new UnsupportedPaymentMethodException(clientId, paymentMethod);
        }

        try {
            paymentDetails.validate();
        } catch (IllegalArgumentException e) {
            return logAndReturn(clientId, null, PaymentResult.failure(ErrorCode.INVALID_PAYMENT_DETAILS, null));
        }

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .clientId(clientId)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .paymentDetails(paymentDetails)
                .build();

        Banks bank = paymentRoutingStrategy.selectBank(paymentRequest, bankService.getAllBanks());
        if (bank == null) {
            return logAndReturn(clientId, null, PaymentResult.failure(ErrorCode.BANK_UNAVAILABLE, null));
        }

        PaymentResult result = bank.processPayment(paymentRequest);
        return logAndReturn(clientId, bank, result);
    }

    public TrafficLogger getTrafficLogger() {
        return trafficLogger;
    }

    private PaymentResult logAndReturn(String clientId, Banks bank, PaymentResult result) {
        trafficLogger.log(clientId, bank, result);
        return result;
    }
}
