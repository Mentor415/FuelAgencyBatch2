package com.faos.service;

import com.faos.exception.InvalidEntityException;
import com.faos.model.Bill;
import com.faos.model.Booking;
import com.faos.model.Customer;
import com.faos.model.Cylinder;
import com.faos.repository.BookingRepository;
import com.faos.repository.CustomerRepository;
import com.faos.repository.CylinderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private CylinderRepository cylinderRepository;

    @Autowired
    private CustomerRepository consumerRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking saveBooking(Booking booking, long consumerId) throws InvalidEntityException {
        Optional<Customer> opt = consumerRepository.findById(consumerId);
        if (opt.isEmpty()) {
            throw new InvalidEntityException("Consumer not found");
        }

        Customer consumer = opt.get();
        booking.setConsumer(consumer);

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        long annualBookings = bookingRepository.countByConsumerIdAndBookingDateAfter(consumerId, oneYearAgo);

        double baseCost = consumer.getConnectionType().equalsIgnoreCase("domestic") ? 1000.0 : 1200.0;
        double additionalCharge = 0.0;

        if (annualBookings >= 6) {
            additionalCharge = baseCost * 0.2;
        }

        double totalAmount = baseCost + additionalCharge;

        Bill bill = new Bill();
        bill.setCost(baseCost);
        bill.setAdditionalCharge(additionalCharge);
        bill.setTotalAmount(totalAmount);
        bill.setBooking(booking);

        booking.setBill(bill);

        Optional<Booking> lastBooking = bookingRepository.findTopByConsumerIdOrderByBookingDateDesc(consumerId);
        if (lastBooking.isPresent()) {
            LocalDate lastBookingDate = lastBooking.get().getBookingDate();
            if (lastBookingDate.plusDays(30).isAfter(LocalDate.now())) {
                throw new InvalidEntityException("You can only book a cylinder 30 days after your last booking.");
            }

            // Return the previously booked cylinder
            Booking previousBooking = lastBooking.get();
            Cylinder previousCylinder = previousBooking.getCylinder();
            if (previousCylinder != null) {
                // Update the previous cylinder status to 'available' and type to 'empty'
                previousCylinder.setStatus("available");
                previousCylinder.setType("empty");
                cylinderRepository.save(previousCylinder);  // Save the updated cylinder status
            }
        }

        // Logic to check for available and full cylinders
        Optional<Cylinder> availableCylinder = cylinderRepository.findFirstByStatusAndTypeOrderById("available", "full");
        if (availableCylinder.isEmpty()) {
            throw new InvalidEntityException("No available and full cylinders found.");
        }

        Cylinder cylinder = availableCylinder.get();
        cylinder.setStatus("delivered");  // Set the cylinder status to delivered
        cylinderRepository.save(cylinder);  // Save the updated cylinder status

        booking.setBookingDate(LocalDate.now());
        booking.setCylinder(cylinder);  // Assign the cylinder to the booking

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) throws InvalidEntityException {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException("Booking not found for ID: " + id));
    }

    public void deleteBooking(Long id) throws InvalidEntityException {
        if (!bookingRepository.existsById(id)) {
            throw new InvalidEntityException("Booking not found for ID: " + id);
        }
        bookingRepository.deleteById(id);
    }

    public List<Booking> getBookingHistoryByConsumerId(Long consumerId) throws InvalidEntityException {
        List<Booking> bookings = bookingRepository.findByConsumerIdOrderByBookingDateDesc(consumerId);
        if (bookings.isEmpty()) {
            throw new InvalidEntityException("No bookings found for Consumer ID: " + consumerId);
        }
        return bookings;
    }
    
}

