package com.springmicroservice.lowleveldesignproblems.onlineauction.models;

public class Buyer extends User{
    public Buyer(String id, String name, String email, String password, String phone, String address, String city, String state, String zip, String country) {
        super(id, name, email, password, phone, address, city, state, zip, country, UserType.BUYER);
    }
}
