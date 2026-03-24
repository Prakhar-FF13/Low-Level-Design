package com.springmicroservice.lowleveldesignproblems.stockexchange.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelOrderRequest(
        @NotBlank(message = "Order ID is required") String orderId,
        @NotBlank(message = "User ID is required") String userId
) {}
