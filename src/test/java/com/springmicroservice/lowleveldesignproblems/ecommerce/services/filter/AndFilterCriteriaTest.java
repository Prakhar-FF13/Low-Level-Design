package com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Product;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.GreaterThanStrategy;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.LessThanStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AndFilterCriteriaTest {

    private List<Product> products;

    @BeforeEach
    void setUp() {
        products = List.of(
                createProduct("A", 10.0),
                createProduct("B", 25.0),
                createProduct("C", 50.0),
                createProduct("D", 75.0),
                createProduct("E", 100.0)
        );
    }

    @Test
    void andFilter_bothCriteriaMatch_returnsIntersection() {
        Criteria minPrice = new PriceFilterCriteria(20.0, new GreaterThanStrategy());
        Criteria maxPrice = new PriceFilterCriteria(80.0, new LessThanStrategy());
        Criteria andCriteria = new AndFilterCriteria(List.of(minPrice, maxPrice));

        List<Product> result = andCriteria.satisfy(products);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(p -> p.getPrice() > 20.0 && p.getPrice() < 80.0));
        assertEquals(List.of("B", "C", "D"), result.stream().map(Product::getName).toList());
    }

    @Test
    void andFilter_noOverlap_returnsEmptyList() {
        Criteria minPrice = new PriceFilterCriteria(60.0, new GreaterThanStrategy());
        Criteria maxPrice = new PriceFilterCriteria(40.0, new LessThanStrategy());
        Criteria andCriteria = new AndFilterCriteria(List.of(minPrice, maxPrice));

        List<Product> result = andCriteria.satisfy(products);

        assertTrue(result.isEmpty());
    }

    @Test
    void andFilter_singleCriteria_returnsThatCriteriaResult() {
        Criteria minPrice = new PriceFilterCriteria(50.0, new GreaterThanStrategy());
        Criteria andCriteria = new AndFilterCriteria(List.of(minPrice));

        List<Product> result = andCriteria.satisfy(products);

        assertEquals(2, result.size());
        assertEquals(List.of("D", "E"), result.stream().map(Product::getName).toList());
    }

    @Test
    void andFilter_emptyProductList_returnsEmptyList() {
        Criteria criteria = new AndFilterCriteria(List.of(
                new PriceFilterCriteria(10.0, new GreaterThanStrategy()),
                new PriceFilterCriteria(100.0, new LessThanStrategy())
        ));
        List<Product> result = criteria.satisfy(List.of());

        assertTrue(result.isEmpty());
    }

    private Product createProduct(String name, double price) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        return p;
    }
}
