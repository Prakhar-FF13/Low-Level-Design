package com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models;

import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.ATM;

public interface ATMState {
    void insertCard(ATM atm, Card card);

    void ejectCard(ATM atm);

    void requestCash(ATM atm, int amount);
}
