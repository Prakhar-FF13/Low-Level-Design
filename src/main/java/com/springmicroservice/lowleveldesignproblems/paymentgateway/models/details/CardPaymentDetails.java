package com.springmicroservice.lowleveldesignproblems.paymentgateway.models.details;

import com.springmicroservice.lowleveldesignproblems.paymentgateway.models.PaymentMethods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardPaymentDetails implements PaymentDetails {
    private String cardNumber;
    private LocalDateTime expiryDate;
    private String cvv;
    private PaymentMethods cardType; // CREDIT_CARD or DEBIT_CARD

    @Override
    public PaymentMethods getPaymentMethod() {
        return cardType;
    }

    @Override
    public void validate() {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalArgumentException("Card number cannot be null or empty");
        }
        if (cardNumber.replaceAll("\\s", "").length() < 13 || cardNumber.replaceAll("\\s", "").length() > 19) {
            throw new IllegalArgumentException("Card number must be 13-19 digits");
        }
        if (expiryDate == null || expiryDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Card expiry must be in the future");
        }
        if (cvv == null || cvv.isBlank()) {
            throw new IllegalArgumentException("CVV cannot be null or empty");
        }
        if (cvv.length() < 3 || cvv.length() > 4 || !cvv.matches("\\d+")) {
            throw new IllegalArgumentException("CVV must be 3 or 4 digits");
        }
        if (cardType != PaymentMethods.CREDIT_CARD && cardType != PaymentMethods.DEBIT_CARD) {
            throw new IllegalArgumentException("Card type must be CREDIT_CARD or DEBIT_CARD");
        }
    }
}
