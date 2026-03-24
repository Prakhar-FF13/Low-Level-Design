package com.springmicroservice.lowleveldesignproblems.stockexchange.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ModifyOrderRequest(
        @NotBlank(message = "Order ID is required") String orderId,
        @NotBlank(message = "User ID is required") String userId,
        @Min(value = 1, message = "Quantity must be at least 1") int quantity,
        @Positive(message = "Price must be positive") double price
) {}
