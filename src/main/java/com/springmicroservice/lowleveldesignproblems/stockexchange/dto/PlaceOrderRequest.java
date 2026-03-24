package com.springmicroservice.lowleveldesignproblems.stockexchange.dto;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.OrderType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlaceOrderRequest(
        @NotBlank(message = "User ID is required") String userId,
        @NotBlank(message = "Stock symbol or ID is required") String stockId,
        @NotNull(message = "Order type is required") OrderType orderType,
        @Min(value = 1, message = "Quantity must be at least 1") int quantity,
        @Positive(message = "Price must be positive") double price
) {}
