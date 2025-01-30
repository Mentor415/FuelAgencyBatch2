package com.faos.controller;

import com.faos.model.Cylinder;
import com.faos.exception.GlobalExceptionHandler;
import com.faos.exception.InvalidEntityException;
import com.faos.service.CylinderService;
import com.faos.service.StockService;
import com.faos.service.EmailService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/cylinders")
public class CylinderController {

    private final CylinderService cylinderService;
    private final StockService stockService;
    private final EmailService emailService;

    public CylinderController(CylinderService cylinderService, StockService stockService,EmailService emailService) {
        this.cylinderService = cylinderService;
        this.stockService = stockService;
        this.emailService=emailService;
    }


 @PostMapping("/add")
public ResponseEntity<Cylinder> addCylinder(@Valid@RequestBody Cylinder cylinder) {
    cylinder.setLastRefillDate(LocalDate.now());
    try {
        Cylinder savedCylinder = cylinderService.saveCylinder(cylinder);
        return ResponseEntity.ok(savedCylinder);
    } catch (InvalidEntityException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}
@PutMapping("/update")
public ResponseEntity<Cylinder> updateCylinder(@RequestBody Cylinder cylinder) {
    try {
        Cylinder existingCylinder = cylinderService.getCylinderById(cylinder.getId());
        Cylinder updatedCylinder = cylinderService.saveCylinder(cylinder);
        return ResponseEntity.ok(updatedCylinder);
    } catch (InvalidEntityException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}
    @PutMapping("/refill/{id}")
    public ResponseEntity<String> refillCylinder(@PathVariable int id) {
        try {
            cylinderService.refillCylinder(id);
            return ResponseEntity.ok("Cylinder refilled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error refilling cylinder: " + e.getMessage());
        }
    }
    
    // Get Cylinder by ID
    @GetMapping("/{id}")
    public Cylinder getCylinderById(@PathVariable int id) throws Exception {
        Cylinder cylinder = cylinderService.getCylinderById(id);
        if (cylinder == null) {
            //throw new GlobalExceptionHandler("Cylinder not found with ID: " + id);
        }
        return cylinder;
    }
     
    @GetMapping("/viewAll")
    public List<Cylinder> getAllCylinders() {
        return cylinderService.getAllCylinders();
    }
    @GetMapping("/viewAllEmpty")
public List<Cylinder> getEmptyAvailableCylinders() {
    return cylinderService.getAllEmptyAvailableCylinders();
}
    
    
    @DeleteMapping("/{id}")
    public String deleteCylinder(@PathVariable int id) {
        try {
            cylinderService.deleteCylinderById(id);
            return "Cylinder deleted successfully.";
        } catch (InvalidEntityException ex) {
           // throw new InvalidEntityException("Cylinder not found with ID: " + id);
        }
                return null;
    }
    @GetMapping("/filter")
    public ResponseEntity<Object> filterCylinders(
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "-1") int query) {  // Default to -1 (invalid ID)
        
        List<Cylinder> cylinders;
        
        // If query is valid (not -1), filter by ID
        if (query != -1) {
            cylinders = cylinderService.filterCylinders(query, type, status, null); // Passing ID filter
        } else {
            // Otherwise, filter by type, status, and query as a string
            cylinders = cylinderService.filterCylinders(null, type, status, null); // No ID filter
        }
    
        // If no cylinders are found, return an error message
        if (cylinders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("error", "Cylinder not found"));
        }
    
        // Return the filtered cylinders
        return ResponseEntity.ok(cylinders);
    }
    
    
    
    @PostMapping("/check-stock")
    public String checkStockManually() {
        stockService.checkCylinderStock();
        return "Cylinder stock verification process has been initiated.";
    }
    @GetMapping("/test-email")
     public String testEmail() {
        emailService.sendEmail("sureharshitha10@gmail.com", "Test Email", "This is a test email.");
        return "Test email sent.";
}

}
