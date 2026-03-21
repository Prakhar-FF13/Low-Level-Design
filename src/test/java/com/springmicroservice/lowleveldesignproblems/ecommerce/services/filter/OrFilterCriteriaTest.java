package com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Product;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.GreaterThanStrategy;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.LessThanStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrFilterCriteriaTest {

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
    void orFilter_eitherCriteriaMatch_returnsUnion() {
        Criteria lowPrice = new PriceFilterCriteria(30.0, new LessThanStrategy());
        Criteria highPrice = new PriceFilterCriteria(70.0, new GreaterThanStrategy());
        Criteria orCriteria = new OrFilterCriteria(List.of(lowPrice, highPrice));

        List<Product> result = orCriteria.satisfy(products);

        assertEquals(4, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getPrice() < 30.0));
        assertTrue(result.stream().anyMatch(p -> p.getPrice() > 70.0));
        assertEquals(List.of("A", "B", "D", "E"), result.stream().map(Product::getName).toList());
    }

    @Test
    void orFilter_bothCriteriaOverlap_returnsDeduplicated() {
        Criteria minPrice = new PriceFilterCriteria(40.0, new GreaterThanStrategy());
        Criteria maxPrice = new PriceFilterCriteria(60.0, new LessThanStrategy());
        Criteria orCriteria = new OrFilterCriteria(List.of(minPrice, maxPrice));

        List<Product> result = orCriteria.satisfy(products);

        // price > 40: C, D, E | price < 60: A, B, C → union: A, B, C, D, E
        assertEquals(5, result.size());
        assertEquals(List.of("A", "B", "C", "D", "E"), result.stream().map(Product::getName).toList());
    }

    @Test
    void orFilter_singleCriteria_returnsThatCriteriaResult() {
        Criteria highPrice = new PriceFilterCriteria(80.0, new GreaterThanStrategy());
        Criteria orCriteria = new OrFilterCriteria(List.of(highPrice));

        List<Product> result = orCriteria.satisfy(products);

        assertEquals(1, result.size());
        assertEquals("E", result.get(0).getName());
    }

    @Test
    void orFilter_noMatches_returnsEmptyList() {
        Criteria lowPrice = new PriceFilterCriteria(5.0, new LessThanStrategy());
        Criteria highPrice = new PriceFilterCriteria(200.0, new GreaterThanStrategy());
        Criteria orCriteria = new OrFilterCriteria(List.of(lowPrice, highPrice));

        List<Product> result = orCriteria.satisfy(products);

        assertTrue(result.isEmpty());
    }

    @Test
    void orFilter_emptyProductList_returnsEmptyList() {
        Criteria orCriteria = new OrFilterCriteria(List.of(
                new PriceFilterCriteria(10.0, new GreaterThanStrategy()),
                new PriceFilterCriteria(100.0, new LessThanStrategy())
        ));
        List<Product> result = orCriteria.satisfy(List.of());

        assertTrue(result.isEmpty());
    }

    private Product createProduct(String name, double price) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        return p;
    }
}
