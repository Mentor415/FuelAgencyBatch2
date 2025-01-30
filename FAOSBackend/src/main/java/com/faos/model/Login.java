package com.faos.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Login {
	@Id
	private Long consumerId;
	private String password;
	private String role;
	 
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "consumer_id", referencedColumnName = "consumerId")
	private Customer customer;

}
