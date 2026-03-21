package com.springmicroservice.lowleveldesignproblems.ecommerce.models;

import lombok.Data;

@Data
public class Product {
    private String productId;
    private String name;
    private String description;
    private double price;
    private Category category;
}
