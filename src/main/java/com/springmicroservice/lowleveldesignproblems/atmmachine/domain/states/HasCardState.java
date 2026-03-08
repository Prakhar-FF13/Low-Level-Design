package com.springmicroservice.lowleveldesignproblems.atmmachine.domain.states;

import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.ATM;
import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models.ATMState;
import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models.Card;

public class HasCardState implements ATMState {

  private final Card currentCard;

  public HasCardState(Card card) {
    this.currentCard = card;
  }

  @Override
  public void insertCard(ATM atm, Card card) {
    throw new IllegalStateException("A card is already inserted in the machine.");
  }

  @Override
  public void ejectCard(ATM atm) {
    System.out.println("Ejecting card inside HasCardState...");
    atm.setState(new IdleState());
  }

  @Override
  public void requestCash(ATM atm, int amount) {
    System.out.println("Processing cash request of amount: " + amount);

    if (atm.getAtmMachineBalance() < amount) {
      System.out.println("Error: Insufficient funds in the ATM. Please eject card or try a lower amount.");
      return;
    }

    System.out.println("Amount is valid. Transitioning to DispenseState.");
    atm.setState(new DispenseState(amount));

    // Auto-dispense since we moved state
    // In reality, this might wait for a hardware callback, but for LLD interview
    // it's synchronous
    atm.getState().requestCash(atm, amount);
  }
}
