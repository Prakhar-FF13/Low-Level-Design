package com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter;

import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.factories.PriceComparisonStrategyFactory;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.PriceComparisonStrategy;
import com.springmicroservice.lowleveldesignproblems.ecommerce.utils.Operator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceComparisonStrategyFactoryTest {

    @Test
    void getPriceComparisonStrategy_returnsStrategyForEachOperator() {
        assertNotNull(PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.Equals));
        assertNotNull(PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.NotEquals));
        assertNotNull(PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.GreaterThan));
        assertNotNull(PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.GreaterThanOrEquals));
        assertNotNull(PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.LessThan));
        assertNotNull(PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.LessThanOrEquals));
    }

    @Test
    void equalsStrategy_comparisonWorks() {
        PriceComparisonStrategy strategy = PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.Equals);
        assertTrue(strategy.compare(42.0, 42.0));
        assertTrue(!strategy.compare(42.0, 43.0));
    }

    @Test
    void greaterThanStrategy_comparisonWorks() {
        PriceComparisonStrategy strategy = PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.GreaterThan);
        assertTrue(strategy.compare(51.0, 50.0));
        assertTrue(!strategy.compare(50.0, 50.0));
    }

    @Test
    void greaterThanOrEqualsStrategy_comparisonWorks() {
        PriceComparisonStrategy strategy = PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.GreaterThanOrEquals);
        assertTrue(strategy.compare(50.0, 50.0));
        assertTrue(strategy.compare(51.0, 50.0));
        assertTrue(!strategy.compare(49.0, 50.0));
    }

    @Test
    void lessThanStrategy_comparisonWorks() {
        PriceComparisonStrategy strategy = PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.LessThan);
        assertTrue(strategy.compare(49.0, 50.0));
        assertTrue(!strategy.compare(50.0, 50.0));
    }

    @Test
    void lessThanOrEqualsStrategy_comparisonWorks() {
        PriceComparisonStrategy strategy = PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.LessThanOrEquals);
        assertTrue(strategy.compare(50.0, 50.0));
        assertTrue(strategy.compare(49.0, 50.0));
        assertTrue(!strategy.compare(51.0, 50.0));
    }

    @Test
    void notEqualsStrategy_comparisonWorks() {
        PriceComparisonStrategy strategy = PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator.NotEquals);
        assertTrue(strategy.compare(42.0, 43.0));
        assertTrue(!strategy.compare(42.0, 42.0));
    }
}
