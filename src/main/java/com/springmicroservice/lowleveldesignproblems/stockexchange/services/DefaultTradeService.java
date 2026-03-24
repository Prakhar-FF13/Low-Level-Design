package com.springmicroservice.lowleveldesignproblems.stockexchange.services;

import java.util.List;
import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Trade;
import com.springmicroservice.lowleveldesignproblems.stockexchange.repository.TradeRepository;

/**
 * Default {@link TradeService} backed by a {@link TradeRepository}.
 */
public class DefaultTradeService implements TradeService {

    private final TradeRepository tradeRepository;

    public DefaultTradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    public void recordTrades(List<Trade> trades) {
        for (Trade trade : trades) {
            tradeRepository.save(trade);
        }
    }

    @Override
    public Optional<Trade> getTrade(String tradeId) {
        return tradeRepository.findById(tradeId);
    }

    @Override
    public List<Trade> getTradesForStock(String stockId) {
        return tradeRepository.findByStockId(stockId);
    }

    @Override
    public List<Trade> getTradesForOrder(String orderId) {
        return tradeRepository.findByOrderId(orderId);
    }
}
