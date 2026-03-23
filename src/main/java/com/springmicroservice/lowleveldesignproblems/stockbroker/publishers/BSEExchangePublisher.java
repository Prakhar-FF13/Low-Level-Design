package com.springmicroservice.lowleveldesignproblems.stockbroker.publishers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.springmicroservice.lowleveldesignproblems.stockbroker.subscribers.Subscriber;
import com.springmicroservice.lowleveldesignproblems.stockbroker.utils.StockUpdate;

public class BSEExchangePublisher implements ExchangePublisher {

    public static final String EXCHANGE_ID = "BSE";

    private final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();

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
