package com.faos.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.faos.model.Customer;
import com.faos.model.Login;
import com.faos.repository.CustomerRepository;
import com.faos.repository.LoginRepository;

import jakarta.mail.MessagingException;

@Service
public class LoginService {

	@Autowired
	private LoginRepository repository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private EmailService emailService;
	private String otp;

	public boolean isFirstLogin(long consumerId, String rawPassword) {
		System.out.println("Raw password   "+rawPassword);
	    Optional<Login> user = repository.findById(consumerId);
	    Optional<Customer> byId = customerRepository.findById(consumerId);

	    if (user.isPresent() && byId.isPresent()) {
	        Customer customer = byId.get();
	        String defaultPassword = generateDefaultPassword(consumerId, customer.getContactNumber());
	        System.out.println("Default  "+defaultPassword);
	        return rawPassword.equals(defaultPassword);
	    }
	    return false;
	}


	public String generateDefaultPassword(Long consumerId, String contactNumber) {
		if (consumerId == null || contactNumber == null) {
			throw new IllegalArgumentException("Consumer ID or contact number cannot be null.");
		}
		String consumerIdStr = String.format("%04d", consumerId % 10000);
		return contactNumber + consumerIdStr;
	}
	
	public void generateAndSendOtp(String email) throws MessagingException {
        otp = String.format("%04d", new Random().nextInt(10000));
        try {
        	emailService.sendEmail(email, otp);
        }catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
    public boolean verifyOtp(String email, String enteredOtp) {
    	if(enteredOtp.equals(otp))return true;
        return false;
    }

    public String updatePassword(Long consumerId, String email, String newPassword) {
        Optional<Login> byId = repository.findById(consumerId);
        Optional<Customer> byId2 = customerRepository.findById(consumerId);

        if (byId.isEmpty() || byId2.isEmpty()) {
            return "Customer not found"; 
        }
        Login login = byId.get();
        Customer customer = byId2.get();
        
        String emailFromDb = customer.getEmail().trim().replaceAll("[^\\w@.]", "").toLowerCase();
        String emailToCompare = email.trim().replaceAll("[^\\w@.]", "").toLowerCase();
        if (consumerId != customer.getConsumerId() || !emailFromDb.equals(emailToCompare)) {
            return "Consumer ID or email does not match"; 
        }
        login.setPassword("{noop}"+newPassword);
        repository.save(login);
        System.out.println("save successfully");
        return "Password updated successfully";
    }

	public boolean status(Long consumerId) {
		Optional<Customer> byId = customerRepository.findById(consumerId);
        Customer customer = byId.get();
        if(customer.isStatus()) return true;
        return false;
	}
}
