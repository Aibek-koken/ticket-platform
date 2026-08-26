package com.smartticket.ticket_platform.controller;

import com.smartticket.ticket_platform.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor 
public class TicketController {

    private final TicketService ticketService;


    @PostMapping("/reserve/{seatId}")
    public ResponseEntity<String> reserveTicket(@PathVariable Long seatId){
        try{
            String result = ticketService.reserveSeat(seatId);
            return ResponseEntity.ok(result);
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/buy/{seatId}")
    public ResponseEntity<String> buyTicket(@PathVariable Long seatId){
        try{
            String result = ticketService.buyTicket(seatId);
            return ResponseEntity.ok(result);
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}
