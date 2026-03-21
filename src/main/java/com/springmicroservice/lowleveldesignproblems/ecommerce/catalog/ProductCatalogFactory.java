package com.springmicroservice.lowleveldesignproblems.ecommerce.catalog;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Category;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Product;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.InMemoryProductRepository;
import com.springmicroservice.lowleveldesignproblems.ecommerce.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates and seeds the in-memory product catalog.
 */
public class ProductCatalogFactory {

    public static ProductRepository createSeededCatalog() {
        List<Product> products = createProducts();
        return new InMemoryProductRepository(products);
    }

    static List<Product> createProducts() {
        Category electronics = new Category();
        electronics.setCategoryId("cat-1");
        electronics.setCategoryName("Electronics");

        Category books = new Category();
        books.setCategoryId("cat-2");
        books.setCategoryName("Books");

        Category clothing = new Category();
        clothing.setCategoryId("cat-3");
        clothing.setCategoryName("Clothing");

        List<Product> catalog = new ArrayList<>();
        catalog.add(createProduct("prod-1", "Laptop", "High-performance laptop", 999.99, electronics));
        catalog.add(createProduct("prod-2", "Smartphone", "Latest smartphone", 699.99, electronics));
        catalog.add(createProduct("prod-3", "Headphones", "Wireless headphones", 149.99, electronics));
        catalog.add(createProduct("prod-4", "Desk Lamp", "LED desk lamp", 29.99, electronics));
        catalog.add(createProduct("prod-5", "Design Patterns", "Gang of Four book", 49.99, books));
        catalog.add(createProduct("prod-6", "Clean Code", "Software craftsmanship", 39.99, books));
        catalog.add(createProduct("prod-7", "T-Shirt", "Cotton t-shirt", 19.99, clothing));
        catalog.add(createProduct("prod-8", "Jeans", "Classic denim jeans", 59.99, clothing));
        catalog.add(createProduct("prod-9", "Winter Jacket", "Warm winter coat", 199.99, clothing));

        return catalog;
    }

    static Product createProduct(String productId, String name, String description, double price, Category category) {
        Product p = new Product();
        p.setProductId(productId);
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setCategory(category);
        return p;
    }
}
