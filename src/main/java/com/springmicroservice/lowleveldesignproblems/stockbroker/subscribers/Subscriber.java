package com.springmicroservice.lowleveldesignproblems.stockbroker.subscribers;

import com.springmicroservice.lowleveldesignproblems.stockbroker.utils.StockUpdate;

public interface Subscriber {
    void update(StockUpdate update);
}
