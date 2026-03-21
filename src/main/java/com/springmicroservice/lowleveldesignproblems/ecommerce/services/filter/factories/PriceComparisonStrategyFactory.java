package com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.factories;

import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.EqualsStrategy;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.GreaterThanOrEqualsStrategy;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.GreaterThanStrategy;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.LessThanOrEqualsStrategy;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.LessThanStrategy;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.NotEqualsStrategy;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.PriceComparisonStrategy;
import com.springmicroservice.lowleveldesignproblems.ecommerce.utils.Operator;

public class PriceComparisonStrategyFactory {
    public static PriceComparisonStrategy getPriceComparisonStrategy(Operator operator) {
        return switch (operator) {
            case Equals -> new EqualsStrategy();
            case NotEquals -> new NotEqualsStrategy();
            case GreaterThan -> new GreaterThanStrategy();
            case GreaterThanOrEquals -> new GreaterThanOrEqualsStrategy();
            case LessThan -> new LessThanStrategy();
            case LessThanOrEquals -> new LessThanOrEqualsStrategy();
        };
    }
}
