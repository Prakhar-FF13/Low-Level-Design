package com.springmicroservice.lowleveldesignproblems.atmmachine.domain;

import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.adapters.IATMBackendApi;
import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models.ATMState;
import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models.Card;
import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.states.IdleState;

public class ATM {
    private ATMState state;
    private int atmMachineBalance;
    private IATMBackendApi backend;

    public ATM(int initialBalance, IATMBackendApi backend) {
        this.atmMachineBalance = initialBalance;
        this.backend = backend;
        this.state = new IdleState();
    }

    public void insertCard(Card card) {
        this.state.insertCard(this, card);
    }

    public void ejectCard() {
        this.state.ejectCard(this);
    }

    public void requestCash(int amount) {
        this.state.requestCash(this, amount);
    }

    public ATMState getState() {
        return state;
    }

    public void setState(ATMState state) {
        this.state = state;
    }

    public int getAtmMachineBalance() {
        return atmMachineBalance;
    }

    public void setAtmMachineBalance(int atmMachineBalance) {
        this.atmMachineBalance = atmMachineBalance;
    }

    public IATMBackendApi getBackend() {
        return backend;
    }

    public void setBackend(IATMBackendApi backend) {
        this.backend = backend;
    }
}
