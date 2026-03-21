package com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Product;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class AndFilterCriteria implements Criteria {
    @NonNull
    private List<Criteria> criteriaList;

    @Override
    public List<Product> satisfy(List<Product> p) {
        return p.stream().filter(product ->
                criteriaList.stream().allMatch(
                        criteria -> !criteria.satisfy(List.of(product)).isEmpty()
                )
        ).collect(Collectors.toList());
    }
}
