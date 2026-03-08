package com.springmicroservice.lowleveldesignproblems.atmmachine.domain.adapters;

import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models.Card;

public interface IATMBackendApi {
    boolean validateCard(Card card);
}
