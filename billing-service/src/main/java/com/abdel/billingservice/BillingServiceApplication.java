package com.abdel.billingservice;

import com.abdel.billingservice.entities.Bill;
import com.abdel.billingservice.entities.ProductItem;
import com.abdel.billingservice.feign.CustomerServiceClient;
import com.abdel.billingservice.feign.InventoryServiceClient;
import com.abdel.billingservice.models.Customer;
import com.abdel.billingservice.models.Product;
import com.abdel.billingservice.repositories.BillRepository;
import com.abdel.billingservice.repositories.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(
            BillRepository billRepository,
            ProductItemRepository productItemRepository,
            CustomerServiceClient customerServiceClient,
            InventoryServiceClient inventoryServiceClient) {
        return args -> {
            Customer customer = customerServiceClient.findCustomerById(1L);

            Bill bill = new Bill();
            bill.setBillingDate(new Date());
            bill.setCustomerID(customer.getId());
            billRepository.save(bill);

            inventoryServiceClient.findAll().getContent().forEach(entityModel -> {
                Product p = entityModel.getContent();
                productItemRepository.save(new ProductItem(
                        null,
                        p.getId(),
                        p.getPrice(),
                        (int) (1 + Math.random() * 1000),
                        null,
                        bill
                ));
            });
        };
    }
}