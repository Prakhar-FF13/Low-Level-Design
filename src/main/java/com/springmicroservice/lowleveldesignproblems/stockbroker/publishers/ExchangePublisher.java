package com.springmicroservice.lowleveldesignproblems.stockbroker.publishers;

import com.springmicroservice.lowleveldesignproblems.stockbroker.subscribers.Subscriber;
import com.springmicroservice.lowleveldesignproblems.stockbroker.utils.StockUpdate;

public interface ExchangePublisher {
    void subscribe(Subscriber subscriber);
    void unsubscribe(Subscriber subscriber);
    void notify(StockUpdate update);
}
