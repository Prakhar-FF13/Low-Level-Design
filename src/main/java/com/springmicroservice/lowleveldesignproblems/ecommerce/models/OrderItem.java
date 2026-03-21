package com.springmicroservice.lowleveldesignproblems.ecommerce.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Snapshot of product at order time (price may change later).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private String productId;
    private String productName;
    private double price;
    private int quantity;

    public double getSubtotal() {
        return price * quantity;
    }
}
