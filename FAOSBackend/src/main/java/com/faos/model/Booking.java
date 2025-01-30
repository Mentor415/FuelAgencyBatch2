package com.faos.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;
    
    private LocalDate bookingDate;

    private LocalDate deliveryDate;

    private String paymentStatus;

    @ManyToOne
    @JoinColumn(name = "consumer_id")
    @JsonIgnoreProperties("bookings") // Prevent infinite loop by ignoring the "bookings" property in Consumer
    private Customer consumer;
    
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    @JsonIgnore 
    private Bill bill;
    
    @OneToOne
    @JoinColumn(name = "cylinder_id", referencedColumnName = "id")
    @JsonIgnoreProperties("bookings") // Prevent infinite loop by ignoring the "bookings" property in Cylinder
    private Cylinder cylinder;

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
    
    public Customer getConsumer() {
        return consumer;
    }

    public void setConsumer(Customer consumer) {
        this.consumer = consumer;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Cylinder getCylinder() {
        return cylinder;
    }

    public void setCylinder(Cylinder cylinder) {
        this.cylinder = cylinder;
    }
}
