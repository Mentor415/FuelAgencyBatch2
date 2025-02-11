package com.faos.controller;

import com.faos.model.CylinderDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

    private String backendBaseUrl = "http://localhost:8080/api/cylinders";

    private final RestTemplate restTemplate;

    public HomeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @GetMapping("/index")
    public String homePage() {
        return "index";
    }

    // Add Cylinder Page
    @GetMapping("/add-cylinder")
    public String showAddCylinderForm(Model model) {
        model.addAttribute("cylinder", new CylinderDTO());  // Use CylinderDTO here
        model.addAttribute("title", "Add Cylinder");
        model.addAttribute("desc", "Fill in the details to add a new cylinder.");
        return "add-cylinder"; // Thymeleaf template name
    }

    @PostMapping("/save-cylinder")
    public String saveCylinder(@ModelAttribute CylinderDTO cylinderDTO, Model model) {
        String apiUrl = backendBaseUrl + "/add";
        try {
            // Send the CylinderDTO as a POST request to the backend and capture the response
            CylinderDTO savedCylinder = restTemplate.postForObject(apiUrl, cylinderDTO, CylinderDTO.class);
    
            // Ensure the response is valid and contains an ID
            if (savedCylinder != null && savedCylinder.getId() != null) {
                model.addAttribute("successMessage", "Cylinder added successfully at ID=" + savedCylinder.getId());
            } else {
                model.addAttribute("errorMessage", "Error adding cylinder. Please try again.");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage","Error adding cylinder Please try again");
        }
    
        // Add necessary attributes for the form
        model.addAttribute("cylinder", new CylinderDTO());
        model.addAttribute("title", "Add Cylinder");
        model.addAttribute("desc", "Fill in the details to add a new cylinder.");
        return "add-cylinder"; // Return to the form
    }
    
    // View Cylinders Page
    @GetMapping("/view-cylinders")
    public String viewCylinders(Model model) {
        String apiUrl = backendBaseUrl + "/viewAll";

        try {
            // Fetch the list of CylinderDTO objects from the backend
            CylinderDTO[] cylinders = restTemplate.getForObject(apiUrl, CylinderDTO[].class);
            model.addAttribute("cylinders", Arrays.asList(cylinders));  // Add list of cylinders to model
        } catch (Exception e) {
            model.addAttribute("errorMessage");
        }
        return "view-cylinders"; // Thymeleaf template name
    }
    //copy
    @GetMapping("/refill-cylinder")
    public String viewCylinder(Model model) {
        String apiUrl = backendBaseUrl + "/viewAllEmpty";

        try {
            // Fetch the list of CylinderDTO objects from the backend
            CylinderDTO[] cylinders = restTemplate.getForObject(apiUrl, CylinderDTO[].class);
            model.addAttribute("cylinders", Arrays.asList(cylinders));  // Add list of cylinders to model
        } catch (Exception e) {
            model.addAttribute("errorMessage");
        }
        return "refill-cylinder"; // Thymeleaf template name
    }
    
    // Filter Cylinders by Type and Status
    @GetMapping("/filterCylinder")
    @ResponseBody
    public List<CylinderDTO> filterCylinders(@RequestParam String type,
                                              @RequestParam String status,
                                              @RequestParam String query) {
        String apiUrl = backendBaseUrl + "/filter?type=" + type + "&status=" + status + "&query=" + query;
        try {
            CylinderDTO[] cylinders = restTemplate.getForObject(apiUrl, CylinderDTO[].class);
            return Arrays.asList(cylinders);
        } catch (Exception e) {
            return List.of();  // Return an empty list on error
        }
    }

    // Delete Cylinder
    @DeleteMapping("/deleteCylinder/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCylinder(@PathVariable Long id) {
        String apiUrl = backendBaseUrl + "/" + id;
        try {
            restTemplate.delete(apiUrl);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update Cylinder
    @PutMapping("/updateCylinder")
    @ResponseBody
    public ResponseEntity<CylinderDTO> updateCylinder(@RequestBody CylinderDTO cylinderDTO) {
        String apiUrl = backendBaseUrl + "/update";
        try {
            restTemplate.put(apiUrl, cylinderDTO);
            return ResponseEntity.ok(cylinderDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Show Update Cylinder Form
    @GetMapping("/update-cylinder/{id}")
    public String showUpdateCylinderForm(@PathVariable Long id, Model model) {
        String apiUrl = backendBaseUrl + "/" + id;
        try {
            CylinderDTO cylinder = restTemplate.getForObject(apiUrl, CylinderDTO.class);
            model.addAttribute("cylinder", cylinder);
            model.addAttribute("title", "Update Cylinder");
            model.addAttribute("desc", "Update the details of the cylinder.");
           
            return "add-cylinder"; // Thymeleaf template name
        } catch (Exception e) {
            model.addAttribute("errorMessage");
            return "redirect:/view-cylinders"; // Redirect back to view cylinders on error
        }
    }
    
    @PutMapping("/refillCylinder/{id}")
	@ResponseBody
	public ResponseEntity<String> refillCylinder(@PathVariable int  id) {
    String apiUrl = backendBaseUrl + "/refill/" + id;
    try {
        restTemplate.put(apiUrl, null);
        return ResponseEntity.ok("Cylinder refilled successfully.");
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to refill cylinder. Please try again.");
       
    }
   

 
}
}
