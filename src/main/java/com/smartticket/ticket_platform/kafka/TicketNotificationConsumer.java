package com.smartticket.ticket_platform.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TicketNotificationConsumer {

    @KafkaListener(topics = "ticket-events", groupId = "smart-ticket-group")
    public void listenTicketPurchasedEvent(String message) {
        System.out.println("📥 [KAFKA CONSUMER] Поймал событие: " + message);
        System.out.println("⏳ [KAFKA CONSUMER] Начинаю тяжелую работу: генерация PDF и отправка email...");
        
        try {
            Thread.sleep(5000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("✅ [KAFKA CONSUMER] Успешно! Письмо с билетом отправлено клиенту.");
        System.out.println("--------------------------------------------------");
    }
}