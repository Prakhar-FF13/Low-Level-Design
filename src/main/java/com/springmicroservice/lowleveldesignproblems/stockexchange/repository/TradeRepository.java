package com.springmicroservice.lowleveldesignproblems.stockexchange.repository;

import java.util.List;
import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Trade;

public interface TradeRepository {

    Trade save(Trade trade);

    Optional<Trade> findById(String tradeId);

    List<Trade> findByStockId(String stockId);

    List<Trade> findByOrderId(String orderId);
}
