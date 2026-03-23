package com.springmicroservice.lowleveldesignproblems.stockbroker;

import com.springmicroservice.lowleveldesignproblems.stockbroker.publishers.BSEExchangePublisher;
import com.springmicroservice.lowleveldesignproblems.stockbroker.publishers.NSEExchangePublisher;
import com.springmicroservice.lowleveldesignproblems.stockbroker.subscribers.StockSubscriber;
import com.springmicroservice.lowleveldesignproblems.stockbroker.utils.Currency;
import com.springmicroservice.lowleveldesignproblems.stockbroker.utils.StockSymbols;
import com.springmicroservice.lowleveldesignproblems.stockbroker.utils.StockUpdate;
import com.springmicroservice.lowleveldesignproblems.stockbroker.utils.Value;

/**
 * Demo demonstrating the Stock Broker LLD with multiple exchanges.
 */
public class StockBrokerDemo {

    public static void main(String[] args) {
        StockSubscriber broker = new StockSubscriber();
        BSEExchangePublisher bse = new BSEExchangePublisher();
        NSEExchangePublisher nse = new NSEExchangePublisher();

        bse.subscribe(broker);
        nse.subscribe(broker);

        // BSE sends AAPL at 150.50
        bse.notify(new StockUpdate(
                BSEExchangePublisher.EXCHANGE_ID,
                StockSymbols.AAPL,
                new Value(Currency.USD, 150.50),
                null));

        // NSE sends AAPL at 151.20 (different exchange, different price)
        nse.notify(new StockUpdate(
                NSEExchangePublisher.EXCHANGE_ID,
                StockSymbols.AAPL,
                new Value(Currency.USD, 151.20),
                null));

        // BSE sends GOOG
        bse.notify(new StockUpdate(
                BSEExchangePublisher.EXCHANGE_ID,
                StockSymbols.GOOG,
                new Value(Currency.USD, 142.75),
                null));

        System.out.println("\n--- Latest Prices ---");
        System.out.println("AAPL: " + broker.getLatestPrice(StockSymbols.AAPL));
        System.out.println("GOOG: " + broker.getLatestPrice(StockSymbols.GOOG));

        System.out.println("\n--- History for AAPL ---");
        broker.getHistory(StockSymbols.AAPL).forEach(u ->
                System.out.println("  " + u.getExchangeId() + " @ " + u.getTimestamp() + ": " + u.getPrice()));
    }
}
