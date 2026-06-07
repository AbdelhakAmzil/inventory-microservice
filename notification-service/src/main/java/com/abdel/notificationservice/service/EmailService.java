package com.abdel.notificationservice.service;

import com.abdel.notificationservice.dto.BillEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.mail.from}")
    private String from;

    @Value("${notification.mail.to}")
    private String to;

    public void sendBillCreatedEmail(BillEvent bill) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("🧾 Nouvelle facture créée — Bill #" + bill.getId());
            message.setText(buildEmailBody(bill));

            mailSender.send(message);
            log.info("✅ Email envoyé pour Bill #{}", bill.getId());

        } catch (Exception e) {
            log.error("❌ Erreur envoi email pour Bill #{} : {}", bill.getId(), e.getMessage());
        }
    }

    private String buildEmailBody(BillEvent bill) {
        return """
                Bonjour,
                
                Une nouvelle facture a été créée dans le système.
                
                ─────────────────────────────
                  Bill ID     : %d
                  Customer ID : %d
                  Date        : %s
                ─────────────────────────────
                
                Cordialement,
                Le système de facturation
                """.formatted(bill.getId(), bill.getCustomerID(), bill.getBillingDate());
    }
}