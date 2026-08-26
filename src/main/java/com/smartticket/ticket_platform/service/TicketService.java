package com.smartticket.ticket_platform.service;

import com.smartticket.ticket_platform.entity.Seat;
import com.smartticket.ticket_platform.entity.SeatStatus;
import com.smartticket.ticket_platform.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import com.smartticket.ticket_platform.kafka.TicketNotificationProducer;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final SeatRepository seatRepository;
    private final StringRedisTemplate redisTemplate;
    private final TicketNotificationProducer notificationProducer;

    private static final String REDIS_KEY_PREFIX = "seat:reservation:";


    public String reserveSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Место не найдено"));

        if (seat.getStatus() == SeatStatus.SOLD) {
            throw new RuntimeException("Извините, место уже продано!");
        }

        String redisKey = REDIS_KEY_PREFIX + seatId;
        Boolean isReserved = redisTemplate.hasKey(redisKey);

        if (Boolean.TRUE.equals(isReserved)) {
            throw new RuntimeException("Место временно забронировано другим пользователем (ждет оплаты)!");
        }

        // Кладем в Redis с TTL 15 минут
        redisTemplate.opsForValue().set(redisKey, "locked", Duration.ofMinutes(15));

        return "Место " + seat.getSeatNumber() + " успешно забронировано на 15 минут!";
    }


    @Transactional
    public String buyTicket(Long seatId){
        // 1. Сначала проверяем кэш: не забронировано ли место прямо сейчас?
        String redisKey = REDIS_KEY_PREFIX + seatId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new RuntimeException("Извините, место находится в процессе оформления другим пользователем!");
        }

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Место не найдено"));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new RuntimeException("Извините, место уже куплено!");
        }

        seat.setStatus(SeatStatus.SOLD);
        seatRepository.save(seat); 
        notificationProducer.sendTicketPurchasedEvent(seatId);

        return "Билет успешно куплен! Место: " + seat.getSeatNumber();
    }
}
