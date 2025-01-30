package com.faos.repository;

import com.faos.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findTopByConsumerIdOrderByBookingDateDesc(Long consumerId);
    long countByConsumerIdAndBookingDateAfter(Long consumerId, LocalDate date);

    // Fetch bookings by consumerId in descending order of bookingDate
    List<Booking> findByConsumerIdOrderByBookingDateDesc(Long consumerId);
}
