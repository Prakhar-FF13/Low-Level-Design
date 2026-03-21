package com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Category;
import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Product;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.factories.PriceComparisonStrategyFactory;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.GreaterThanStrategy;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.LessThanStrategy;
import com.springmicroservice.lowleveldesignproblems.ecommerce.utils.Operator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceFilterCriteriaTest {

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
    void priceGreaterThan_filterReturnsProductsAboveThreshold() {
        PriceFilterCriteria criteria = new PriceFilterCriteria(50.0, new GreaterThanStrategy());
        List<Product> result = criteria.satisfy(products);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getPrice() > 50.0));
        assertEquals("D", result.get(0).getName());
        assertEquals("E", result.get(1).getName());
    }

    @Test
    void priceLessThan_filterReturnsProductsBelowThreshold() {
        PriceFilterCriteria criteria = new PriceFilterCriteria(50.0, new LessThanStrategy());
        List<Product> result = criteria.satisfy(products);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getPrice() < 50.0));
        assertEquals("A", result.get(0).getName());
        assertEquals("B", result.get(1).getName());
    }

    @Test
    void priceGreaterThan_withNoMatches_returnsEmptyList() {
        PriceFilterCriteria criteria = new PriceFilterCriteria(200.0, new GreaterThanStrategy());
        List<Product> result = criteria.satisfy(products);

        assertTrue(result.isEmpty());
    }

    @Test
    void priceLessThan_withAllMatches_returnsAllProducts() {
        PriceFilterCriteria criteria = new PriceFilterCriteria(200.0, new LessThanStrategy());
        List<Product> result = criteria.satisfy(products);

        assertEquals(5, result.size());
    }

    @Test
    void priceGreaterThan_withEmptyList_returnsEmptyList() {
        PriceFilterCriteria criteria = new PriceFilterCriteria(10.0, new GreaterThanStrategy());
        List<Product> result = criteria.satisfy(List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void priceFilter_withFactoryGreaterThan_worksCorrectly() {
        PriceFilterCriteria criteria = new PriceFilterCriteria(
                25.0,
                PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.GreaterThan)
        );
        List<Product> result = criteria.satisfy(products);

        assertEquals(3, result.size());
    }

    @Test
    void priceFilter_withFactoryLessThan_worksCorrectly() {
        PriceFilterCriteria criteria = new PriceFilterCriteria(
                75.0,
                PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.LessThan)
        );
        List<Product> result = criteria.satisfy(products);

        // Products < 75: A(10), B(25), C(50) = 3 products
        assertEquals(3, result.size());
    }

    private Product createProduct(String name, double price) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        return p;
    }
}
