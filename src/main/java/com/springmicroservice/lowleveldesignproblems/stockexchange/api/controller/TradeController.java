package com.springmicroservice.lowleveldesignproblems.stockexchange.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springmicroservice.lowleveldesignproblems.stockexchange.dto.TradeResponse;
import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Trade;
import com.springmicroservice.lowleveldesignproblems.stockexchange.services.TradeService;

@RestController
@RequestMapping("/api/exchange/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/{tradeId}")
    public ResponseEntity<TradeResponse> getTrade(@PathVariable String tradeId) {
        return tradeService.getTrade(tradeId)
                .map(t -> ResponseEntity.ok(toResponse(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TradeResponse>> listTrades(
            @RequestParam(required = false) String stockId,
            @RequestParam(required = false) String orderId) {
        if (orderId != null && !orderId.isBlank()) {
            return ResponseEntity.ok(
                    tradeService.getTradesForOrder(orderId).stream().map(TradeController::toResponse).toList());
        }
        if (stockId != null && !stockId.isBlank()) {
            return ResponseEntity.ok(
                    tradeService.getTradesForStock(stockId).stream().map(TradeController::toResponse).toList());
        }
        throw new IllegalArgumentException("Query parameter stockId or orderId is required");
    }

    private static TradeResponse toResponse(Trade t) {
        return new TradeResponse(
                t.getId(),
                null,
                t.getBuyerOrderId(),
                t.getSellerOrderId(),
                t.getStockId(),
                t.getQuantity(),
                t.getPrice(),
                t.getCreatedAt());
    }
}
