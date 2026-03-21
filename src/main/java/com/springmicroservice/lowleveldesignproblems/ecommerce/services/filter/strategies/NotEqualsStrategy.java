package com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies;

public class NotEqualsStrategy implements PriceComparisonStrategy {
    @Override
    public boolean compare(double p1, double p2) {
        return Double.compare(p1, p2) != 0;
    }
}
