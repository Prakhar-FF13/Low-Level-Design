package com.springmicroservice.lowleveldesignproblems.paymentgateway;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.Banks;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentMethods;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentResult;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.details.CardPaymentDetails;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.details.NetBankingPaymentDetails;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.details.UPIPaymentDetails;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.repository.BankRepository;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.repository.ClientsRepository;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.repository.impl.InMemoryBankRepository;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.repository.impl.InMemoryClientsRepository;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.services.BankService;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.services.ClientService;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.services.TrafficLogger;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.strategies.PaymentModeRoutingStrategy;
import com.springmicroservice.lowleveldesignproblems.paymentgateway.strategies.PaymentRoutingStrategy;

public class Main {
    public static void main(String[] args) {
        ClientsRepository clientsRepository = new InMemoryClientsRepository();
        BankRepository bankRepository = new InMemoryBankRepository();

        ClientService clientService = new ClientService(clientsRepository);
        BankService bankService = new BankService(bankRepository);

        // Create banks: HDFC (CC/Debit), ICICI (UPI/NetBanking), with 20% failure rate
        Banks hdfc = bankService.createBank("HDFC", 0.2,
                List.of(PaymentMethods.CREDIT_CARD, PaymentMethods.DEBIT_CARD));
        Banks icici = bankService.createBank("ICICI", 0.2,
                List.of(PaymentMethods.UPI, PaymentMethods.NET_BANKING));

        // Routing: PaymentMode (CC→HDFC, UPI→ICICI) or Weighted (30% HDFC, 70% ICICI for overlapping methods)
        Map<PaymentMethods, String> modeConfig = Map.of(
                PaymentMethods.CREDIT_CARD, hdfc.getBankId(),
                PaymentMethods.DEBIT_CARD, hdfc.getBankId(),
                PaymentMethods.UPI, icici.getBankId(),
                PaymentMethods.NET_BANKING, icici.getBankId()
        );
        PaymentRoutingStrategy routingStrategy = new PaymentModeRoutingStrategy(modeConfig);

        PaymentGatewayOrchestrator orchestrator = new PaymentGatewayOrchestrator(
                clientService, bankService, routingStrategy);

        // Onboard client
        String clientId = orchestrator.onboardClient("Merchant1", List.of(PaymentMethods.UPI, PaymentMethods.CREDIT_CARD));
        System.out.println("--- Onboarded client: " + clientId + " (Merchant1) ---");

        // Capture UPI payment
        System.out.println("\n--- Capture UPI payment ---");
        UPIPaymentDetails upiDetails = new UPIPaymentDetails("merchant@icici");
        PaymentResult upiResult = orchestrator.capturePayment(clientId, 100.0, PaymentMethods.UPI, upiDetails);
        printResult(upiResult);

        // Capture Credit Card payment
        System.out.println("\n--- Capture Credit Card payment ---");
        CardPaymentDetails cardDetails = new CardPaymentDetails(
                "4111111111111111",
                LocalDateTime.now().plusYears(2),
                "123",
                PaymentMethods.CREDIT_CARD
        );
        PaymentResult ccResult = orchestrator.capturePayment(clientId, 250.0, PaymentMethods.CREDIT_CARD, cardDetails);
        printResult(ccResult);

        // Capture NetBanking payment (client doesn't support - will throw)
        System.out.println("\n--- Attempt NetBanking (client doesn't support) ---");
        try {
            NetBankingPaymentDetails nbDetails = new NetBankingPaymentDetails("user", "pass");
            orchestrator.capturePayment(clientId, 50.0, PaymentMethods.NET_BANKING, nbDetails);
        } catch (Exception e) {
            System.out.println("  Expected: " + e.getMessage());
        }

        // Traffic logs
        System.out.println("\n--- Traffic logs ---");
        TrafficLogger logger = orchestrator.getTrafficLogger();
        logger.getLogs().forEach(entry ->
                System.out.println("  " + entry.timestamp() + " | client=" + entry.clientId() + " | bank=" + entry.bankId()
                        + " | success=" + entry.result().isSuccess() + " | txnId=" + entry.result().getTransactionId()));

        // Remove client
        System.out.println("\n--- Remove client ---");
        orchestrator.removeClient(clientId);
        System.out.println("  Client " + clientId + " removed.");
    }

    private static void printResult(PaymentResult result) {
        System.out.println("  Success: " + result.isSuccess()
                + " | TransactionId: " + result.getTransactionId()
                + " | Bank: " + result.getBankId()
                + (result.getErrorCode() != null ? " | Error: " + result.getErrorCode() : ""));
    }
}
