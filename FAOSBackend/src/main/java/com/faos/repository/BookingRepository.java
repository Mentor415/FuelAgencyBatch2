package com.faos.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.faos.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	@Query("SELECT b FROM Booking b WHERE b.consumer.consumerId = :consumerId ORDER BY b.bookingDate DESC")
    Optional<Booking> findTopByConsumerIdOrderByBookingDateDesc(Long consumerId);
	
	@Query("SELECT COUNT(b) FROM Booking b WHERE b.consumer.consumerId = :consumerId AND b.bookingDate > :date")
    long countByConsumerIdAndBookingDateAfter(Long consumerId, LocalDate date);

    // Fetch bookings by consumerId in descending order of bookingDate
	@Query("SELECT b FROM Booking b WHERE b.consumer.consumerId = :consumerId ORDER BY b.bookingDate DESC")
    List<Booking> findByConsumerIdOrderByBookingDateDesc(Long consumerId);
}