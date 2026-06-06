package com.abdel.billingservice.producer;

import com.abdel.billingservice.entities.Bill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillProducer {

    private final KafkaTemplate<String, Bill> kafkaTemplate;

    public void sendBillCreatedEvent(Bill bill) {
        log.info("📤 Sending bill-created event for bill id: {}", bill.getId());
        Message<Bill> message = MessageBuilder
                .withPayload(bill)
                .setHeader(KafkaHeaders.TOPIC, "bill-created")
                .build();
        kafkaTemplate.send(message);
    }
}