package com.faos.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Customer {

	@Id
    @Column(unique = true, nullable = false, length = 17)
    private Long consumerId;

	@NotBlank(message = "Name is mandatory")
    @Size(min = 3, max = 50, message = "Invalid name length")
    @Pattern(regexp = "^[A-Za-z\\s'-]+$", message = "Name must contain only alphabets, spaces, hyphens, or apostrophes")
    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 150, nullable = false)
    @NotBlank(message = "Address is mandatory")
    @Size(min = 5, max = 50, message = "Invalid name length")
    private String address;

    @NotNull
    @Column(length = 10)
    @NotBlank(message = "Contact number can't be Empty")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid contact number")
    private String contactNumber;

    @NotNull
    @Column(length = 50)
    @Email(message = "Invalid email address")
    @NotBlank(message = "Email can't be Empty")
    private String email;
    
    @NotBlank(message="Connection Type is required")
    private String connectionType;
    @NotBlank(message="Gender is required")
    private String gender;
    
    private boolean status;
    private LocalDate registrationDate;
    private LocalDate deactivationDate;
    private String reasonForDeactivation;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("ConsumerObject")
    private List<Booking> bookings= new ArrayList<>();
    
}
