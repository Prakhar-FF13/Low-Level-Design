package com.springmicroservice.lowleveldesignproblems.stockexchange.models;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @NotBlank
    private String symbol;

    @NotBlank
    private String name;
    private double price;
    private Instant createdAt;
    private Instant updatedAt;
}
