package com.abdel.notificationservice.consumer;

import com.abdel.notificationservice.dto.BillEvent;
import com.abdel.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "bill-created", groupId = "notification-group")
    public void handleBillCreated(BillEvent bill) {
        log.info("📥 Notification reçue — Nouvelle facture créée !");
        log.info("   ► Bill ID     : {}", bill.getId());
        log.info("   ► Customer ID : {}", bill.getCustomerID());
        log.info("   ► Date        : {}", bill.getBillingDate());

        // Envoyer l'email
        emailService.sendBillCreatedEmail(bill);
    }
}