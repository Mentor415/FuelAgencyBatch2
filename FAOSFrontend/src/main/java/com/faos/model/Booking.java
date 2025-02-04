package com.faos.model;

import java.time.LocalDate;

public class Booking {

    private Long bookingId;
    
    private LocalDate bookingDate;

    private LocalDate deliveryDate;

    private String paymentStatus;

    private Consumer consumer;

    // Getters and Setters

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public Object getBill() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBill'");
    }
}