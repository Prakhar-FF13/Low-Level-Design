package com.springmicroservice.lowleveldesignproblems.stockexchange.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @NotBlank
    private String userId;

    @NotBlank
    private OrderType type;
    @NotBlank
    private String stockId;
    @Min(0)
    private int filledQuantity;
    @Min(0)
    private int reamingingQuantity;
    private double price;
    @Builder.Default
    private Instant createdAt = Instant.now();
    @Builder.Default
    private OrderStatus status = OrderStatus.ACCEPTED;
}
