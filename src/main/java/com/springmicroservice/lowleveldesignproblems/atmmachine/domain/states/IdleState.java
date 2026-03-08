package com.springmicroservice.lowleveldesignproblems.atmmachine.domain.states;

import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.ATM;
import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models.ATMState;
import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models.Card;

public class IdleState implements ATMState {

  @Override
  public void insertCard(ATM atm, Card card) {
    System.out.println("Processing card inside IdleState...");
    if (atm.getBackend().validateCard(card)) {
      System.out.println("Card validation successful. Transitioning to HasCardState.");
      atm.setState(new HasCardState(card));
    } else {
      System.out.println("Card validation failed. Ejecting card.");
      throw new IllegalStateException("Card validation failed");
    }
  }

  @Override
  public void ejectCard(ATM atm) {
    throw new IllegalStateException("No card to eject in Idle State.");
  }

  @Override
  public void requestCash(ATM atm, int amount) {
    throw new IllegalStateException("Insert card first before requesting cash.");
  }
}
