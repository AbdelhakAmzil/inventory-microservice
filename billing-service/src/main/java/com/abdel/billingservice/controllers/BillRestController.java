package com.abdel.billingservice.controllers;

import com.abdel.billingservice.entities.Bill;
import com.abdel.billingservice.feign.CustomerServiceClient;
import com.abdel.billingservice.feign.InventoryServiceClient;
import com.abdel.billingservice.repositories.BillRepository;
import com.abdel.billingservice.repositories.ProductItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BillRestController {

    private final BillRepository billRepository;
    private final ProductItemRepository productItemRepository;
    private final CustomerServiceClient customerServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    @GetMapping("/bills/full/{id}")
    public Bill getBill(@PathVariable Long id) {
        Bill bill = billRepository.findById(id).get();
        bill.setCustomer(customerServiceClient.findCustomerById(bill.getCustomerID()));
        bill.setProductItems(productItemRepository.findByBillId(id));
        bill.getProductItems().forEach(pi ->
                pi.setProduct(inventoryServiceClient.findProductById(pi.getProductID()))
        );
        return bill;
    }
}