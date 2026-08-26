package com.smartticket.ticket_platform.repository;

import com.smartticket.ticket_platform.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    
}
