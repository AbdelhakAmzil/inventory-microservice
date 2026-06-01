package com.abdel.customerservice.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "fullCustomer",types =
        Customer.class)
interface CustomerProjection extends Projection{
    public Long getId();
    public String getName();
    public String getEmail();
}
