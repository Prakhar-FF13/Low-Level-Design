package com.springmicroservice.lowleveldesignproblems.stockexchange.services;

import java.util.List;
import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Trade;

/**
 * Port for persisting and querying executed trades. {@link ExchangeService} depends on this abstraction (DIP).
 */
public interface TradeService {

    void recordTrades(List<Trade> trades);

    Optional<Trade> getTrade(String tradeId);

    List<Trade> getTradesForStock(String stockId);

    List<Trade> getTradesForOrder(String orderId);
}
