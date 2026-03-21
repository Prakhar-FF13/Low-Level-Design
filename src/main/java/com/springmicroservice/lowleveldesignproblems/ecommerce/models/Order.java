package com.springmicroservice.lowleveldesignproblems.ecommerce.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private String orderId;
    private String userId;
    private OrderStatus status;
    private List<OrderItem> items = new ArrayList<>();
    private long createdAt;

    public Order(String orderId, String userId) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = OrderStatus.PLACED;
        this.createdAt = System.currentTimeMillis();
    }

    public double getTotalAmount() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PLACED;
    }
}
