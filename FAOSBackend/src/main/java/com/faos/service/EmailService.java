package com.faos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.faos.model.Customer;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    private SimpleMailMessage message;
    private final String adminEmail = "admin@gmail.com"; // Receiver's email address

    public void sendEmailToAdmin(Customer savedCustomer) {
    	String text = String.format(
    		    "A new customer has been added successfully.\n\n" +
    		    "Here are the customer details:\n\n" +
    		    "Customer ID: %s\n" + "Name: %s\n",
    		    savedCustomer.getConsumerId(), savedCustomer.getName());



        message= new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("New Customer Added Successfully");
        message.setText(text);
        mailSender.send(message);
    }
    
    public void sendEmailToCustomer(String email, String password, long consumerId) {
        String text = String.format(
            "Congratulations! You are Registered Successfully\n" +
            "Customer ID: %s\n" +
            "Your Default Password: %s\n", 
            consumerId, 
            password
        );
        message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Register Successfully");
        message.setText(text);
        mailSender.send(message);
    }
    
    public void sendEmail(String to, String subject, String body) {
        System.out.println("Preparing to send email...");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("Email sent successfully to " + to);
    }
}
