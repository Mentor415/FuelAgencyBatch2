package com.faos.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.faos.model.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Controller
public class CustomerController {

	private final String BASE_URL = "http://localhost:8080";
	private RestTemplate restTemplate;
	private Customer customer;
	private String backendUrl;
	private ResponseEntity<Customer> response;

	@GetMapping("/")
	public String get() {
		return "HomePage";
	}

	@GetMapping("/Menu_page")
	public String gets() {
		return "Menu_page";
	}

	@GetMapping("/customerDetails")
	public String findAll(Model model) {
		restTemplate = new RestTemplate();
		backendUrl = BASE_URL + "/findAll";

		ResponseEntity<List<Customer>> response = restTemplate.exchange(backendUrl, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Customer>>() {
				});
		List<Customer> customers = response.getBody();
		model.addAttribute("customers", customers);
		return "Customer_Details";
	}

	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("customer", new Customer());
		return "index_customer";
	}

	@PostMapping("/addCustomer")
	public String registerCustomer(@ModelAttribute("customer") Customer customer, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		restTemplate = new RestTemplate();
		backendUrl = BASE_URL + "/addCustomer";
		try {
			response = restTemplate.postForEntity(backendUrl, customer, Customer.class);
			if (response.getStatusCode() == HttpStatus.CREATED) {
				redirectAttributes.addFlashAttribute("message","Customer registered successfully with ID: " + response.getBody().getConsumerId());
			    return "redirect:/register";
			}
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
				Map<String, String> errors = null;
				try {
					errors = new ObjectMapper().readValue(ex.getResponseBodyAsString(),
							new TypeReference<Map<String, String>>() {
							});
					for (Map.Entry<String, String> entryset : errors.entrySet()) {
						bindingResult.rejectValue(entryset.getKey(), "", entryset.getValue());
					}
				} catch (JsonProcessingException e) {
					model.addAttribute("message", "Failed to parse error response from server.");
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException("An unexpected error occurred.Please try again.");
		}
		return "index_customer";
	}

	@GetMapping("/editRedirect")
	public String editRedirect(@RequestParam(value = "consumerId", required = false) String consumerId, Model model) {
		model.addAttribute("consumerId", consumerId);
		return "edit_customer";
	}

	@GetMapping("/find")
	public String findByConsumerId(@RequestParam("id") Long id, Model model) {
		RestTemplate restTemplate = new RestTemplate();
		String backendUrl = BASE_URL + "/find/" + id;

		try {
			response = restTemplate.getForEntity(backendUrl, Customer.class);
			customer = response.getBody();

			if (response.getStatusCode() == HttpStatus.OK && customer != null) {
				if (!customer.isStatus()) {
					model.addAttribute("message", "Customer is Deactivated");
				}
				model.addAttribute("customer", customer);
			} else {
				model.addAttribute("message", "Customer not found");
			}
		} catch (HttpClientErrorException.NotFound ex) {
			model.addAttribute("message", "Customer not found");
		} catch (Exception ex) {
			model.addAttribute("message", "An unexpected error occurred. Please try again.");
		}
		return "edit_customer";
	}

	@PostMapping("/activate")
	public String activateCustomer(@RequestParam("consumerId") Long id, Model model) {
		String message = activateCustomerBackendCall(id, model);
		model.addAttribute("message", message);
		return "edit_customer";
	}

	@PostMapping("/activateCustomer/")
	public String activateCustomerDetail(@RequestParam("consumerId") Long id, Model model) {
		String message = activateCustomerBackendCall(id, model);
		model.addAttribute("message", message);
		return "Customer_Details";
	}

	private String activateCustomerBackendCall(Long id, Model model) {
		restTemplate = new RestTemplate();
		String backendUrl = BASE_URL + "/activate/" + id;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

			ResponseEntity<String> response = restTemplate.exchange(backendUrl, HttpMethod.PUT, requestEntity,
					String.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				return response.getBody();
			} else {
				return "Failed to activate customer. Please try again later.";
			}
		} catch (HttpClientErrorException.NotFound ex) {
			return "No customer found with ID: " + id;
		} catch (HttpClientErrorException.Conflict ex) {
			return "Customer is already activated.";
		} catch (Exception ex) {
			return "An unexpected error occurred. Please try again.";
		}
	}

	@PostMapping("/updateCustomer")
	public String updateCustomer(@RequestParam("consumerId") Long id, @ModelAttribute Customer customer,Model model) {
		String backendUrl = BASE_URL + "/updateCustomer?consumerId=" + id;

		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Customer> requestEntity = new HttpEntity<>(customer, headers);

			ResponseEntity<Customer> response = restTemplate.exchange(backendUrl, HttpMethod.POST, requestEntity,
					Customer.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				model.addAttribute("customer", response.getBody());
				model.addAttribute("message", "Customer Updated Successfully");
			} else {
				model.addAttribute("message", "Failed to update customer.");
			}
		} catch (HttpClientErrorException.NotFound ex) {
			model.addAttribute("message", "No customer found with ID: " + id);
		} catch (Exception ex) {
			model.addAttribute("message", "An unexpected error occurred. Please try again.");
		}

		return "edit_customer";
	}

	@GetMapping("/deactivate")
	public String deactivate() {
		return "Deactivate_customer";
	}

	@PostMapping("/deactivate/customer")
	public String deactivateCustomer(@RequestParam("consumerId") Long id,@RequestParam("reasonForDeactivation") String reason, Model model) {
		restTemplate = new RestTemplate();
		backendUrl = BASE_URL + "/deactivate/customer?consumerId=" + id + "&reasonForDeactivation=" + reason;

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(backendUrl, null, String.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				model.addAttribute("message", response.getBody());
			} else {
				model.addAttribute("message", "Failed to deactivate customer.");
			}
		} catch (HttpClientErrorException.NotFound ex) {
			throw new NoSuchElementException("No customer found with ID: " + id);
		} catch (Exception ex) {
			throw new RuntimeException("An unexpected error occurred.Please try again.");
		}

		return "Deactivate_customer";
	}
	
	@DeleteMapping("/deleteCustomer/")
	public String deleteBy(@RequestParam("consumerId") Long id, Model model) {
		String backendUrl = BASE_URL + "/deleteCustomer/" + id;
		RestTemplate restTemplate = new RestTemplate();

		try {
			restTemplate.delete(backendUrl);
			model.addAttribute("message", "Customer Deleted Successfully");
		} catch (HttpClientErrorException.NotFound ex) {
			throw new NoSuchElementException("No customer found with ID: " + id);
		} catch (Exception ex) {
			throw new RuntimeException("An unexpected error occurred. Please try again.");
		}
		return "Customer_Details";
	}
}
