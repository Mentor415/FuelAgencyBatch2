package com.faos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.faos.exception.InvalidEntityException;
import com.faos.model.Bill;
import com.faos.model.Booking;
import com.faos.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Fetch all bookings
 // Fetch all bookings with consumer ID
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();

        // Map Booking entities to BookingResponse DTOs
//        List<BookingResponse> responses = bookings.stream()
//            .map(booking -> new BookingResponse(
//                booking.getBookingId(),
//                booking.getConsumer().getId(), // Get consumerId
//                booking.getBookingDate(),
//                booking.getDeliveryDate(),
//                booking.getPaymentStatus()
//            ))
//            .toList();

        return ResponseEntity.ok(bookings);
    }


    // Fetch booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) throws InvalidEntityException {
        try {
            Booking booking = bookingService.getBookingById(id);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Booking not found for ID: " + id);
        }
    }

    // Create a new booking
    @PostMapping("/addBooking/{id}")
    public ResponseEntity<String> createBooking(@RequestBody Booking booking, @PathVariable long id) {
        try {
            Booking savedBooking = bookingService.saveBooking(booking, id);
            return ResponseEntity.ok("Booking successful. Booking ID: " + savedBooking.getBookingId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Generate bill by booking ID
    @PostMapping("/generateBill/{bookingId}")
    public ResponseEntity<?> generateBill(@PathVariable Long bookingId) throws InvalidEntityException {
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            Bill bill = booking.getBill();
            if (bill == null) {
                return ResponseEntity.status(404).body("Bill not found for Booking ID: " + bookingId);
            }
            return ResponseEntity.ok(
                "Bill Details:\n" +
                "Total Amount: " + bill.getTotalAmount() + "\n" +
                "Cost: " + bill.getCost() + "\n" +
                "Additional Charge: " + bill.getAdditionalCharge()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Booking not found for ID: " + bookingId);
        }
    }

    // Delete a booking by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id) throws InvalidEntityException {
        bookingService.deleteBooking(id);
        return ResponseEntity.status(404).body("Booking " + id + " is deleted.");
    }
 
    // Fetch booking history by consumer ID
    @GetMapping("/history/{consumerId}")
    public ResponseEntity<?> getBookingHistoryByConsumerId(@PathVariable Long consumerId) throws InvalidEntityException {
        try {
            List<Booking> bookings = bookingService.getBookingHistoryByConsumerId(consumerId);

            // Map Booking entities to BookingResponse DTOs
//            List<BookingResponse> responses = bookings.stream()
//                .map(booking -> new BookingResponse(
//                    booking.getBookingId(),
//                    booking.getConsumer().getId(),
//                    booking.getBookingDate(),
//                    booking.getDeliveryDate(),
//                    booking.getPaymentStatus()
//                ))
//                .toList();
//
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("No bookings found for Consumer ID: " + consumerId);
        }
    }

}
