package com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Product;

public interface PriceComparisonStrategy {
    boolean compare(double p1, double p2);
}
