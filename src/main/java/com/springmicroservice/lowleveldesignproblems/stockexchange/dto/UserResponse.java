package com.springmicroservice.lowleveldesignproblems.stockexchange.dto;

/**
 * User details for responses (aligns with README: id, name, phone, email).
 */
public record UserResponse(
        String userId,
        String userName,
        String phoneNumber,
        String emailId
) {}
