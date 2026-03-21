package com.springmicroservice.lowleveldesignproblems.onlineauction.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {
    protected String id;
    protected String name;
    protected String email;
    protected String password;
    protected String phone;
    protected String address;
    protected String city;
    private String state;
    protected String zip;
    protected String country;
    protected UserType userType;
}
