package com.abdel.billingservice.entities;

import com.abdel.billingservice.models.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date billingDate;
    private long customerID;

    @OneToMany(mappedBy = "bill")
    private Collection<ProductItem> productItems;

    @Transient
    private Customer customer;
}