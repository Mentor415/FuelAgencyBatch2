package com.faos.controller;

import com.faos.dto.BookingDTO;
import com.faos.model.Bill;
import com.faos.model.Booking;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;

@Controller
public class BookingController {

    private final RestTemplate restTemplate;
    private final String backendUrl = "http://localhost:8080/api/bookings";

    public BookingController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Home Page
    @GetMapping("/home")
    public String homePage() {
        return "Home";
    }

    // Booking Form
    @GetMapping("/bookings")
    public String showBookingForm(Model model) {
        model.addAttribute("booking", new Booking());
        return "bookingForm";
    }

    // Handle Booking Submission
    @PostMapping("/book-cylinder")
    public String handleBookingSubmission(
            @RequestParam long consumerId,
            @RequestParam String deliveryDate,
            @RequestParam String paymentStatus,
            Model model) {

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setConsumerId(consumerId);
        bookingDTO.setDeliveryDate(LocalDate.parse(deliveryDate));
        bookingDTO.setPaymentStatus(paymentStatus);

        try {
            String url = backendUrl + "/addBooking";
            ResponseEntity<String> response = restTemplate.postForEntity(url, bookingDTO, String.class);
            String responseBody = response.getBody();
            model.addAttribute("message", responseBody);

            // Extract Booking ID
            String bookingId = responseBody.split(":")[1].trim();
            model.addAttribute("bookingId", bookingId);

            return "bookingConfirmation";
        } catch (HttpClientErrorException.BadRequest e) {
            // Catch InvalidEntityException response from backend
            model.addAttribute("error","Invalid booking: "+ e.getResponseBodyAsString());
            return "errorPage";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "errorPage";
        }
    }

    // Generate Bill with exception handling
    @GetMapping("/generate-bill")
    public String generateBill(@RequestParam(required = false) Long bookingId, Model model) {
        if (bookingId == null || bookingId <= 0) {
            model.addAttribute("error", "Invalid Booking ID provided.");
            return "errorPage";
        }

        try {
            String url = backendUrl + "/generateBill/" + bookingId;
            ResponseEntity<Bill> response = restTemplate.getForEntity(url, Bill.class);

            Bill bill = response.getBody();
            if (bill == null) {
                model.addAttribute("error", "No bill found for the provided Booking ID.");
                return "errorPage";
            }

            model.addAttribute("bill", bill);
            return "billDetails";
        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("error", "Booking not found: " + e.getResponseBodyAsString());
            return "errorPage";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to generate bill: " + e.getMessage());
            return "errorPage";
        }
    }

    // View Booking History
@GetMapping("/booking-history")
public String viewBookingHistory(@RequestParam(required = false) Long consumerId, Model model) {
    if (consumerId == null) {
        model.addAttribute("error", "Please enter a Consumer ID to fetch booking history.");
        return "bookingHistory";
    }

    try {
        String url = backendUrl + "/history/" + consumerId;
        ResponseEntity<Booking[]> response = restTemplate.getForEntity(url, Booking[].class);

        Booking[] bookings = response.getBody();
        if (bookings == null || bookings.length == 0) {
            model.addAttribute("error", "No booking history found for Consumer ID: " + consumerId);
        } else {
            model.addAttribute("bookings", bookings);
        }

        return "bookingHistory";
    } catch (HttpClientErrorException.NotFound e) {
        // Handle case where no booking history is found
        model.addAttribute("error", "No booking history found: " + e.getResponseBodyAsString());
        return "errorPage";
    } catch (HttpClientErrorException.BadRequest e) {
        // Handle InvalidEntityException response from backend
        model.addAttribute("error", "Invalid request: "+ e.getResponseBodyAsString());
        return "errorPage";
    } catch (Exception e) {
        model.addAttribute("error", "Error fetching booking history: " + e.getMessage());
        return "errorPage";
    }
}
}

