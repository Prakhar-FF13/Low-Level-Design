package com.springmicroservice.lowleveldesignproblems.ecommerce.repository;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Product;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    public InMemoryProductRepository() {
    }

    public InMemoryProductRepository(List<Product> initialProducts) {
        if (initialProducts != null) {
            initialProducts.forEach(p -> products.put(p.getProductId(), p));
        }
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    @Override
    public Optional<Product> findById(String productId) {
        return Optional.ofNullable(products.get(productId));
    }
}
