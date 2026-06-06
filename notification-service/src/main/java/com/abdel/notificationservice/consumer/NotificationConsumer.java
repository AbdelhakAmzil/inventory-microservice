package com.abdel.notificationservice.consumer;

import com.abdel.notificationservice.dto.BillEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationConsumer {

    @KafkaListener(topics = "bill-created", groupId = "notification-group")
    public void handleBillCreated(BillEvent bill) {
        log.info("📥 Notification reçue — Nouvelle facture créée !");
        log.info("   ► Bill ID     : {}", bill.getId());
        log.info("   ► Customer ID : {}", bill.getCustomerID());
        log.info("   ► Date        : {}", bill.getBillingDate());
        // Ici vous pouvez : envoyer un email, SMS, push notification...
    }
}