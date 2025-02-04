package com.faos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.faos.model.Bill;
import com.faos.model.Booking;
import com.faos.service.BookingService;
import com.faos.dto.BookingDTO;  // Import the BookingDTO
import com.faos.exception.InvalidEntityException;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Fetch all bookings
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    // Fetch booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        try {
            Booking booking = bookingService.getBookingById(id);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Booking not found for ID: " + id);
        }
    }

    // Create a new booking with BookingDTO
    @PostMapping("/addBooking")
    public ResponseEntity<String> createBooking(@RequestBody BookingDTO bookingDTO) {
        try {
            Booking savedBooking = bookingService.saveBooking(bookingDTO);
            return ResponseEntity.ok("Booking successful. Your Booking ID is: " + savedBooking.getBookingId());
        } catch (InvalidEntityException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Return only the message
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred: " + e.getMessage());
        }
    }


    // Generate bill by booking ID

    @GetMapping("/generateBill/{bookingId}")
    public ResponseEntity<?> generateBill(@PathVariable Long bookingId) {
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            Bill bill = booking.getBill();

            if (bill == null) {
                return ResponseEntity.status(404).body("Bill not found for Booking ID: " + bookingId);
            }

            return ResponseEntity.ok(bill); // Return Bill as JSON response
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Booking not found for ID: " + bookingId);
        }
    }


    // Delete a booking by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.status(404).body("Booking " + id + " is deleted.");
    }

    // Fetch booking history by consumer ID
    @GetMapping("/history/{consumerId}")
    public ResponseEntity<?> getBookingHistoryByConsumerId(@PathVariable Long consumerId) {
        try {
            List<Booking> bookings = bookingService.getBookingHistoryByConsumerId(consumerId);
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("No bookings found for Consumer ID: " + consumerId);
        }
    }


}
