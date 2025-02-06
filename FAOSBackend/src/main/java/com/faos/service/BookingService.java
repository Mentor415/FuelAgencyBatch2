package com.faos.service;

import com.faos.model.BookingDTO;
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

    public Booking saveBooking(BookingDTO bookingDTO) throws InvalidEntityException {
        Optional<Customer> opt = consumerRepository.findById(bookingDTO.getConsumerId());
        if (opt.isEmpty()) {
            throw new InvalidEntityException("Consumer not found");
        }

        Customer consumer = opt.get();
        // Fetch the most recent booking of the consumer
        Optional<Booking> lastBookingOpt = bookingRepository.findTopByConsumerIdOrderByBookingDateDesc(bookingDTO.getConsumerId());

        if (lastBookingOpt.isPresent()) {
            Booking lastBooking = lastBookingOpt.get();
            LocalDate lastBookingDate = lastBooking.getBookingDate();
            LocalDate currentDate = LocalDate.now();

            if (lastBookingDate.plusDays(30).isAfter(currentDate)) {
                throw new InvalidEntityException("You can only book a cylinder 30 days after your last booking. Last booking date: " + lastBookingDate);
            }
        }
        Booking booking = new Booking();
        booking.setConsumer(consumer);
        booking.setBookingDate(LocalDate.now()); // Set current date as booking date
        booking.setDeliveryDate(bookingDTO.getDeliveryDate());
        booking.setPaymentStatus(bookingDTO.getPaymentStatus());

        // Cylinder logic remains the same...
        Optional<Cylinder> availableCylinder = cylinderRepository.findFirstByStatusAndTypeOrderById("available", "full");
        if (availableCylinder.isEmpty()) {
            throw new InvalidEntityException("No available and full cylinders found.");
        }

        Cylinder cylinder = availableCylinder.get();
        cylinder.setStatus("delivered");
        cylinderRepository.save(cylinder);

        booking.setCylinder(cylinder);

        // Logic for the Bill
        double baseCost = consumer.getConnectionType().equalsIgnoreCase("domestic") ? 1000.0 : 1200.0;
        double additionalCharge = 0.0;

        // Check if the consumer has booked more than 6 times in the past year
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        long annualBookings = bookingRepository.countByConsumerIdAndBookingDateAfter(bookingDTO.getConsumerId(), oneYearAgo);
        if (annualBookings >= 6) {
            additionalCharge = baseCost * 0.2;  // 20% additional charge
        }

        double totalAmount = baseCost + additionalCharge;
        Bill bill = new Bill();
        bill.setCost(baseCost);
        bill.setAdditionalCharge(additionalCharge);
        bill.setTotalAmount(totalAmount);
        bill.setBooking(booking);

        booking.setBill(bill);

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
