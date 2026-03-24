package com.springmicroservice.lowleveldesignproblems.stockexchange.models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String name;
    private String email;
}
