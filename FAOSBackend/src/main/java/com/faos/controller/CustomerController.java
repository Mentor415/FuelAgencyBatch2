package com.faos.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.faos.model.Customer;
import com.faos.service.CustomerService;

import jakarta.validation.Valid;

@RestController
public class CustomerController {

	@Autowired
	private CustomerService service;

	private Optional<Customer> optionalCustomer;
	private Customer customer;
	
	//Checking ID is proper or not
	private void idCheck(Long id) {
		 if (id == null || id <= 0 || String.valueOf(id).length() != 17) 
		        throw new IllegalArgumentException("Invalid ID: must be a positive number and exactly 17 digits long.");	    
	}
	
	//Just For testing
	@GetMapping("/")
	public ResponseEntity<String> get() {
		return ResponseEntity.ok("Welcome to backend controller");
	}

	//Find All Customer
	@GetMapping("/findAll")
	public ResponseEntity<List<Customer>> findAll() {
	    List<Customer> customers = service.findAll();
	    return ResponseEntity.ok(customers);
	}
	
	//Find Customer By their ID
	@GetMapping("/find/{id}")
	public ResponseEntity<?> findByConsumerId(@PathVariable("id") Long id) {
	    idCheck(id);
	    optionalCustomer = service.findByConsumerId(id);
	    if (optionalCustomer.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Not Found");
	    }
	    customer = optionalCustomer.get();
	    return ResponseEntity.ok(customer);
	}


	//Add New Customer
	@PostMapping("/addCustomer")
	public ResponseEntity<?> registerCustomer(@Valid @RequestBody Customer customer, BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
	        Map<String, String> errors = bindingResult.getFieldErrors().stream()
	                .collect(Collectors.toMap(
	                        fieldError -> fieldError.getField(),
	                        fieldError -> fieldError.getDefaultMessage()
	                ));
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	    }
	    try {
	         customer = service.registerCustomer(customer);
	        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
	    }
	}

	
	// Activate customer
	@PutMapping("/activate/{id}")
	public ResponseEntity<?> activateCustomer(@PathVariable("id") Long id) {
	    idCheck(id);
        optionalCustomer = service.findByConsumerId(id);
	    if (optionalCustomer.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Not Found");
	    }

	    customer = optionalCustomer.get();
	    if (customer.isStatus()) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("Customer is already activated");
	    }

	    customer.setStatus(true);
	    customer.setDeactivationDate(null);
	    customer.setReasonForDeactivation(null);
	    service.save(customer);

	    return ResponseEntity.ok("Customer activated successfully");
	}


    // Update customer details
	@PostMapping("/updateCustomer")
	public ResponseEntity<?> updateCustomer(@RequestParam("consumerId") Long id, @RequestBody Customer customer) {
		idCheck(id);
	    customer = service.updateCustomer(id, customer);
	    return ResponseEntity.ok(customer);
	}

    // Deactivate customer
    @PostMapping("/deactivate/customer")
    public ResponseEntity<?> deactivateCustomer(@RequestParam("consumerId") Long id,
                                                @RequestParam("reasonForDeactivation") String reason) {
    	idCheck(id);
        optionalCustomer = service.findByConsumerId(id);

        if (optionalCustomer.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Not Found");       

        customer = optionalCustomer.get();
        customer.setStatus(false);
        customer.setDeactivationDate(LocalDate.now());
        customer.setReasonForDeactivation(reason);
        service.save(customer);

        return ResponseEntity.ok("Customer Successfully Deactivated");
    }
    
    //Delete Customer By their ID
    @DeleteMapping("/deleteCustomer/{consumerId}")
    public ResponseEntity<?> deleteById(@PathVariable("consumerId") Long id) {
        idCheck(id);
        if ("ok".equals(service.deleteById(id))) {
            return ResponseEntity.ok("Customer Deleted Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Not Found");
        }
    }
    
    // Count Total number of Customer 
    @GetMapping("/count")
    public ResponseEntity<String> countCustomers() {
        return ResponseEntity.ok("Total number of customers is: " + service.countCustomers());
    }
    
}
