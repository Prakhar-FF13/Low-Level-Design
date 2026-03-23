package com.springmicroservice.lowleveldesignproblems.stockbroker.publishers;

import java.util.concurrent.CopyOnWriteArrayList;

import com.springmicroservice.lowleveldesignproblems.stockbroker.subscribers.Subscriber;
import com.springmicroservice.lowleveldesignproblems.stockbroker.utils.StockUpdate;

public class NSEExchangePublisher implements ExchangePublisher {

    public static final String EXCHANGE_ID = "NSE";

    private final java.util.List<Subscriber> subscribers = new CopyOnWriteArrayList<>();

    @Override
    public void subscribe(Subscriber subscriber) {
        if (subscriber != null) {
            subscribers.add(subscriber);
        }
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notify(StockUpdate update) {
        StockUpdate withExchange = update.getExchangeId() == null
                ? new StockUpdate(EXCHANGE_ID, update.getSymbol(), update.getPrice(), update.getTimestamp())
                : update;
        for (Subscriber subscriber : subscribers) {
            subscriber.update(withExchange);
        }
    }
}
