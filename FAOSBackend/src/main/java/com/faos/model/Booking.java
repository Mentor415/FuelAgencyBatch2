package com.faos.model;


import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private LocalDate bookingDate;
    @Column(nullable = false)
    private LocalDate deliveryDate;
    
    @Column(nullable = false)
    private String cylinderType;
    
    @Column(nullable = false)
    private double cash;

    @Column(nullable = false)
    private double price;

    private String paymentStatus;
    
    @ManyToOne
    @JoinColumn(name = "consumer_id", nullable = false)
    @JsonIgnoreProperties("bookingList")
    private Customer customer;
}


