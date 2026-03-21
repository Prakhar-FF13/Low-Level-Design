package com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies;

public class LessThanStrategy implements PriceComparisonStrategy {
    @Override
    public boolean compare(double p1, double p2) {
        return p1 < p2;
    }
}
