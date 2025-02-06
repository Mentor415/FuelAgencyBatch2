package com.faos.model;

import java.time.LocalDate;
import lombok.Data;


@Data
public class Customer {

	private Long consumerId;

    private String name;

    private String address;

    private String contactNumber;

    private String email;

    private String connectionType;
    private String gender;

    private boolean status;
    private LocalDate registrationDate;
    private LocalDate deactivationDate;
    private String reasonForDeactivation;

}
