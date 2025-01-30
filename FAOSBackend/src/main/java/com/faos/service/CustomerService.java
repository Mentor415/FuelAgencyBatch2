package com.faos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.faos.model.Customer;
import com.faos.model.Login;
import com.faos.repository.CustomerRepository;
import com.faos.repository.LoginRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private LoginRepository loginRepository;
    
    private Long generateUniqueDigitId() {
        long timestamp = System.currentTimeMillis(); 
        int randomDigits = new Random().nextInt(9000) + 1000; 
        return Long.parseLong(timestamp + "" + randomDigits); 
    }

    public List<Customer> findAll() {
        return repository.findAll();
    }

    public Optional<Customer> findByConsumerId(Long id) {
        return repository.findByConsumerId(id);
    }

    public Customer registerCustomer(Customer customer) {
        customer.setConsumerId(generateUniqueDigitId());
        customer.setStatus(true);
        customer.setRegistrationDate(LocalDate.now());

        Customer savedCustomer = repository.save(customer);
        System.out.println(savedCustomer+"added");

        String defaultPassword = customer.getContactNumber() +
                String.valueOf(customer.getConsumerId()).substring(
                String.valueOf(customer.getConsumerId()).length() - 4);
        
        Login login = new Login();
        login.setConsumerId(savedCustomer.getConsumerId());
        login.setPassword("{noop}"+defaultPassword);
        login.setRole("ROLE_CUSTOMER"); 
        login.setCustomer(savedCustomer);
        Login save = loginRepository.save(login); 
        System.out.println(save+"added");


        // Send email notification
        try {
            emailService.sendEmailToAdmin(savedCustomer);
            emailService.sendEmailToCustomer(customer.getEmail(), defaultPassword, customer.getConsumerId());
            System.out.println("Success send email");
        } catch (Exception e) {
            System.out.println("Failed send email");
            throw new RuntimeException("Email service failed: " + e.getMessage());
        }

        return savedCustomer;
    }

    
    public Customer updateCustomer(Long id, Customer dto) {
    	
        Customer customer = repository.findByConsumerId(id).orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setName(dto.getName());
        customer.setContactNumber(dto.getContactNumber());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());
        customer.setConnectionType(dto.getConnectionType());
        return repository.save(customer);
    }
    
    public Customer deactivateCustomer(Long id, String reason) {
        Customer customer = repository.findByConsumerId(id).orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setStatus(false);
        customer.setDeactivationDate(LocalDate.now());
        customer.setReasonForDeactivation(reason);
        return repository.save(customer);
    }
    
    public String deleteById(Long id) {
        Optional<Customer> customer = repository.findById(id); 
        
        if (customer.isPresent()) { 
            repository.deleteById(id);
            loginRepository.deleteById(id);
            return "ok"; 
        }
        
        return "not found"; 
    }
    
    
    public long countCustomers() {
        return repository.count();
    }
    
    public Customer save(Customer customer) {
        System.out.println("addedc different location");

        return repository.save(customer);
    }
}
