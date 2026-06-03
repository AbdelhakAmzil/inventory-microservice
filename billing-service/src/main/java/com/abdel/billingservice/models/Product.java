package com.abdel.billingservice.models;

import lombok.Data;

@Data
public class Product {
    private Long id;
    private String name;
    private double price;
}