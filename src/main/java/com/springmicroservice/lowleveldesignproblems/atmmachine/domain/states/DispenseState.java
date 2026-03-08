package com.springmicroservice.lowleveldesignproblems.atmmachine.domain.states;

import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.ATM;
import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models.ATMState;
import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models.Card;

public class DispenseState implements ATMState {

  private final int dispenseAmount;

  public DispenseState(int amount) {
    this.dispenseAmount = amount;
  }

  @Override
  public void insertCard(ATM atm, Card card) {
    throw new IllegalStateException("Cannot insert card during dispensation.");
  }

  @Override
  public void ejectCard(ATM atm) {
    throw new IllegalStateException("Card will be ejected automatically after dispensing cash.");
  }

  @Override
  public void requestCash(ATM atm, int amount) {
    System.out.println("Processing inside DispenseState...");
    System.out.println("Dispensing out physical cash: " + dispenseAmount);

    // Deduct from atm balance
    atm.setAtmMachineBalance(atm.getAtmMachineBalance() - dispenseAmount);
    System.out.println("Remaining ATM machine balance is: " + atm.getAtmMachineBalance());

    System.out.println("Automatically ejecting card and transitioning to IdleState.");
    atm.setState(new IdleState());
  }
}
