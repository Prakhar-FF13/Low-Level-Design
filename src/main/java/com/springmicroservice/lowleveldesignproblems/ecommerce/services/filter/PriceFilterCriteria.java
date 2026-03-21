package com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Product;
import com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter.strategies.PriceComparisonStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class PriceFilterCriteria implements Criteria {
    private double price;

    private PriceComparisonStrategy priceComparisonStrategy;

    @Override
    public List<Product> satisfy(List<Product> p) {
        return p.stream().filter(product -> priceComparisonStrategy.compare(
                product.getPrice(),
                this.price
        )).collect(Collectors.toList());
    }
}
