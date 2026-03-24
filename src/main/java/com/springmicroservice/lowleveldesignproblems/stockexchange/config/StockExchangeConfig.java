package com.springmicroservice.lowleveldesignproblems.stockexchange.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.springmicroservice.lowleveldesignproblems.stockexchange.orderbook.IOrderBook;
import com.springmicroservice.lowleveldesignproblems.stockexchange.orderbook.impl.OrderBook;
import com.springmicroservice.lowleveldesignproblems.stockexchange.repository.InMemoryTradeRepository;
import com.springmicroservice.lowleveldesignproblems.stockexchange.repository.TradeRepository;
import com.springmicroservice.lowleveldesignproblems.stockexchange.services.DefaultTradeService;
import com.springmicroservice.lowleveldesignproblems.stockexchange.services.ExchangeService;
import com.springmicroservice.lowleveldesignproblems.stockexchange.services.OrderMatchingExecutor;
import com.springmicroservice.lowleveldesignproblems.stockexchange.services.TradeService;
import com.springmicroservice.lowleveldesignproblems.stockexchange.services.strategies.FifoOrderMatchingStrategy;
import com.springmicroservice.lowleveldesignproblems.stockexchange.services.strategies.OrderMatchingStrategy;

@Configuration
public class StockExchangeConfig {

    @Bean
    public IOrderBook orderBook() {
        return new OrderBook();
    }

    @Bean
    public TradeRepository tradeRepository() {
        return new InMemoryTradeRepository();
    }

    @Bean
    public TradeService tradeService(TradeRepository tradeRepository) {
        return new DefaultTradeService(tradeRepository);
    }

    @Bean
    public OrderMatchingStrategy orderMatchingStrategy() {
        return new FifoOrderMatchingStrategy();
    }

    @Bean
    public OrderMatchingExecutor orderMatchingExecutor(
            IOrderBook orderBook,
            OrderMatchingStrategy orderMatchingStrategy,
            TradeService tradeService) {
        return new OrderMatchingExecutor(orderBook, orderMatchingStrategy, tradeService);
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService stockExchangeExecutor() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public ExchangeService exchangeService(
            IOrderBook orderBook,
            OrderMatchingExecutor orderMatchingExecutor,
            ExecutorService stockExchangeExecutor) {
        return new ExchangeService(orderBook, orderMatchingExecutor, stockExchangeExecutor);
    }
}
