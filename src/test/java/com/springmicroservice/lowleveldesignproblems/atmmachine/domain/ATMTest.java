package com.springmicroservice.lowleveldesignproblems.atmmachine.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.adapters.AlwaysTrueATMBackendAPI;
import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.adapters.IATMBackendApi;
import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models.Card;

public class ATMTest {
  IATMBackendApi mockBackend = new AlwaysTrueATMBackendAPI();

  @Test
  public void testATMSuccessCase() {
    ATM atm = new ATM(1000, mockBackend);
    // below moves from idle to has card state
    atm.insertCard(new Card("123456789", "1234", "Chase Bank", "Prakhar"));

    // below first moves from has card to dispense state and then back to idle
    atm.requestCash(200);

    // below moves from idle to has card state
    atm.insertCard(new Card("123456789", "1234", "Chase Bank", "Prakhar"));
  }

  @Test
  public void testNoDuplicateTransactions() {
    ATM atm = new ATM(1000, mockBackend);
    // below moves from idle to has card state
    atm.insertCard(new Card("123456789", "1234", "Chase Bank", "Prakhar"));

    assertThrows(IllegalStateException.class,
        () -> atm.insertCard(new Card("123456789", "1234", "Chase Bank", "Prakhar")));
  }

  @Test
  public void testNoCashWithdrawalWithoutCard() {
    ATM atm = new ATM(1000, mockBackend);
    assertThrows(IllegalStateException.class, () -> atm.requestCash(200));
  }

  @Test
  public void testNoCashWithdrawalWithInvalidCard() {
    IATMBackendApi mockBackend = new IATMBackendApi() {
      @Override
      public boolean validateCard(Card card) {
        return false;
      }
    };
    ATM atm = new ATM(1000, mockBackend);
    assertThrows(IllegalStateException.class,
        () -> atm.insertCard(
            new Card("123456789", "1234", "Chase Bank", "Prakhar")));
  }
}
