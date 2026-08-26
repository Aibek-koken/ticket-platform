package com.smartticket.ticket_platform.kafka;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketNotificationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendTicketPurchasedEvent(Long seatId){
        String topic = "ticket-events";
        String key = "seat-" + seatId;
        String message = "Оплачено билет на место №" + seatId + "Запустите генерацию PDF";
        
        kafkaTemplate.send(topic,key, message);
        System.out.println("🚀 [KAFKA PRODUCER] Улетело в топик: " + message + " | Ключ: " + key);
    }
    
    
}
