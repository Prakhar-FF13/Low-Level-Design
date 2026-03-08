package com.springmicroservice.lowleveldesignproblems.atmmachine.domain.adapters;

import com.springmicroservice.lowleveldesignproblems.atmmachine.domain.models.Card;

public class AlwaysTrueATMBackendAPI implements IATMBackendApi{
    @Override
    public boolean validateCard(Card card) {
        return true;
    }
}
