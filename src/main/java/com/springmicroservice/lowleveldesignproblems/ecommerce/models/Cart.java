package com.springmicroservice.lowleveldesignproblems.ecommerce.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Cart {
    private String cartId;
    private String userId;
    private List<CartItem> items = new ArrayList<>();

    public Cart(String cartId, String userId) {
        this.cartId = cartId;
        this.userId = userId;
    }
}
