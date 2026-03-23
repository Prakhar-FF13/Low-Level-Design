package com.springmicroservice.lowleveldesignproblems.stockbroker.subscribers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.springmicroservice.lowleveldesignproblems.stockbroker.utils.StockSymbols;
import com.springmicroservice.lowleveldesignproblems.stockbroker.utils.StockUpdate;
import com.springmicroservice.lowleveldesignproblems.stockbroker.utils.Value;

public class StockSubscriber implements Subscriber {

    private static final int MAX_HISTORY_SIZE = 100;

    private final Map<StockSymbols, Value> latestPrice = new ConcurrentHashMap<>();
    private final Map<StockSymbols, ConcurrentLinkedDeque<StockUpdate>> history = new ConcurrentHashMap<>();

    @Override
    public void update(StockUpdate update) {
        if (update == null) return;

        StockSymbols symbol = update.getSymbol();
        Value price = update.getPrice();

        latestPrice.put(symbol, price);
        history.computeIfAbsent(symbol, k -> new ConcurrentLinkedDeque<>()).addLast(update);

        var symbolHistory = history.get(symbol);
        while (symbolHistory.size() > MAX_HISTORY_SIZE) {
            symbolHistory.pollFirst();
        }

        System.out.println("StockSubscriber [" + update.getExchangeId() + "]: " + symbol + " = " + price);
    }

    public Value getLatestPrice(StockSymbols symbol) {
        return latestPrice.get(symbol);
    }

    public Map<StockSymbols, Value> getStockData() {
        return Collections.unmodifiableMap(new ConcurrentHashMap<>(latestPrice));
    }

    public List<StockUpdate> getHistory(StockSymbols symbol) {
        var symbolHistory = history.get(symbol);
        return symbolHistory == null
                ? List.of()
                : List.copyOf(symbolHistory);
    }
}
