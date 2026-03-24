package com.springmicroservice.lowleveldesignproblems.stockexchange.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Trade;

public class InMemoryTradeRepository implements TradeRepository {

    private final Map<String, Trade> byId = new ConcurrentHashMap<>();

    @Override
    public Trade save(Trade trade) {
        byId.put(trade.getId(), trade);
        return trade;
    }

    @Override
    public Optional<Trade> findById(String tradeId) {
        return Optional.ofNullable(byId.get(tradeId));
    }

    @Override
    public List<Trade> findByStockId(String stockId) {
        return byId.values().stream()
                .filter(t -> stockId.equals(t.getStockId()))
                .toList();
    }

    @Override
    public List<Trade> findByOrderId(String orderId) {
        List<Trade> out = new ArrayList<>();
        for (Trade t : byId.values()) {
            if (orderId.equals(t.getBuyerOrderId()) || orderId.equals(t.getSellerOrderId())) {
                out.add(t);
            }
        }
        return out;
    }
}
