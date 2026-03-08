package com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models;

public class Card {
    private String cardNumber;
    private String pin;
    private String bank;
    private String name;

    public Card(String cardNumber, String pin, String bank, String name) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.bank = bank;
        this.name = name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
