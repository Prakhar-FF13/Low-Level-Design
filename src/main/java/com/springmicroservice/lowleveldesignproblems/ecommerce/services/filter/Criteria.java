package com.springmicroservice.lowleveldesignproblems.ecommerce.services.filter;

import com.springmicroservice.lowleveldesignproblems.ecommerce.models.Product;

import java.util.List;

public interface Criteria {
    List<Product> satisfy(List<Product> p);
}
