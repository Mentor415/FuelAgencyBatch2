package com.faos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.faos.model.Cylinder;
import com.faos.repository.CylinderRepository;
@Service
public class StockService {
    @Autowired
    private final CylinderRepository cylinderRepository;
    @Autowired
    private final EmailService emailService;
    

    private static final int THRESHOLD = 10;

    
    public StockService(CylinderRepository cylinderRepository, EmailService emailService) {
        this.cylinderRepository = cylinderRepository;
        this.emailService = emailService;
    }
  
    @Scheduled(cron = "0 0 18 * * ?") 
    public void checkCylinderStock() {
        System.out.println("Scheduled task is running...");
        List<Cylinder> cylinders = cylinderRepository.findAll();

        long availableAndFullCount = cylinders.stream()
                .filter(c -> "available".equalsIgnoreCase(c.getStatus()) || "full".equalsIgnoreCase(c.getStatus()))
                .count();

        long totalCylinders = cylinders.size();

        if (totalCylinders - availableAndFullCount < THRESHOLD) {
            String subject = "Low Cylinder Stock Warning";
            String message = String.format(
                "Alert! Stock running low.\n\nTotal Cylinders: %d\nAvailable and Full Cylinders: %d\nPlease reorder immediately.",
                totalCylinders, availableAndFullCount);
            emailService.sendEmail("sureharshitha10@gmail.com", subject, message);
        }
    }
}
