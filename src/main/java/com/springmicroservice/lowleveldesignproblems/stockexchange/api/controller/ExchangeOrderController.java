package com.springmicroservice.lowleveldesignproblems.stockexchange.api.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springmicroservice.lowleveldesignproblems.stockexchange.dto.ModifyOrderRequest;
import com.springmicroservice.lowleveldesignproblems.stockexchange.dto.OrderResponse;
import com.springmicroservice.lowleveldesignproblems.stockexchange.dto.PlaceOrderRequest;
import com.springmicroservice.lowleveldesignproblems.stockexchange.exceptions.OrderNotFoundException;
import com.springmicroservice.lowleveldesignproblems.stockexchange.exceptions.UnauthorizedOrderAccessException;
import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Order;
import com.springmicroservice.lowleveldesignproblems.stockexchange.orderbook.IOrderBook;
import com.springmicroservice.lowleveldesignproblems.stockexchange.services.ExchangeService;

@RestController
@RequestMapping("/api/exchange/orders")
@Validated
public class ExchangeOrderController {

    private final ExchangeService exchangeService;
    private final IOrderBook orderBook;

    public ExchangeOrderController(ExchangeService exchangeService, IOrderBook orderBook) {
        this.exchangeService = exchangeService;
        this.orderBook = orderBook;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        Order order = Order.builder()
                .userId(request.userId())
                .type(request.orderType())
                .stockId(request.stockId())
                .filledQuantity(0)
                .reamingingQuantity(request.quantity())
                .price(request.price())
                .build();
        exchangeService.placeOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        Order order = orderBook.findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return ResponseEntity.ok(toResponse(order));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> modifyOrder(
            @PathVariable String orderId,
            @Valid @RequestBody ModifyOrderRequest request) {
        if (!orderId.equals(request.orderId())) {
            throw new IllegalArgumentException("Path orderId must match body orderId");
        }
        Order existing = orderBook.findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (!existing.getUserId().equals(request.userId())) {
            throw new UnauthorizedOrderAccessException(orderId, request.userId());
        }
        int filled = existing.getFilledQuantity();
        if (request.quantity() < filled) {
            throw new IllegalArgumentException("New quantity cannot be less than filled quantity: " + filled);
        }
        Order updated = Order.builder()
                .id(existing.getId())
                .userId(existing.getUserId())
                .type(existing.getType())
                .stockId(existing.getStockId())
                .filledQuantity(filled)
                .reamingingQuantity(request.quantity() - filled)
                .price(request.price())
                .createdAt(existing.getCreatedAt())
                .status(existing.getStatus())
                .build();
        exchangeService.modifyOrder(updated);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable String orderId,
            @RequestParam @NotBlank(message = "userId is required") String userId) {
        Order existing = orderBook.findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (!existing.getUserId().equals(userId)) {
            throw new UnauthorizedOrderAccessException(orderId, userId);
        }
        exchangeService.cancelOrder(orderId, existing.getStockId());
        return ResponseEntity.noContent().build();
    }

    private static OrderResponse toResponse(Order o) {
        int totalQuantity = o.getFilledQuantity() + o.getReamingingQuantity();
        return new OrderResponse(
                o.getId(),
                o.getUserId(),
                o.getType(),
                o.getStockId(),
                totalQuantity,
                o.getFilledQuantity(),
                o.getReamingingQuantity(),
                o.getPrice(),
                o.getCreatedAt(),
                o.getStatus());
    }
}
