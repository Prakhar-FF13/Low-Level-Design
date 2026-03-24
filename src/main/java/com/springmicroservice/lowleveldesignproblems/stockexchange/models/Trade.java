package com.springmicroservice.lowleveldesignproblems.stockexchange.models;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Trade {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    @NotBlank
    private String buyerOrderId;
    @NotBlank
    private String sellerOrderId;
    @NotBlank
    private String stockId;
    @Min(1)
    private int quantity;
    @Min(0)
    private double price;
    @Builder.Default
    private Instant createdAt = Instant.now();
}
