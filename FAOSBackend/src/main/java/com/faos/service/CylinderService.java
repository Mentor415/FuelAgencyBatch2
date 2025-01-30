package com.faos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.faos.exception.InvalidEntityException;
import com.faos.model.Cylinder;
import com.faos.repository.CylinderRepository;

@Service
public class CylinderService {

    @Autowired
    private final CylinderRepository cylinderRepository;

    public CylinderService(CylinderRepository cylinderRepository) {
        this.cylinderRepository = cylinderRepository;
    }

    public Cylinder saveCylinder(Cylinder cylinder) throws InvalidEntityException {
        // Validate the cylinder
        if (cylinder == null) {
            throw new InvalidEntityException("Cylinder cannot be null.");
        }
        Cylinder savedCylinder = cylinderRepository.save(cylinder);
        return savedCylinder;
        
    }
    
    public List<Cylinder> getAllEmptyAvailableCylinders() {
        return cylinderRepository.findByTypeAndStatus("empty", "available");
    }
    
    public Cylinder getCylinderById(Integer cylinderId) throws InvalidEntityException{
        if (cylinderId == null || cylinderId <= 0) {
            throw new InvalidEntityException("Invalid cylinder ID.");
        }
        return cylinderRepository.findById(cylinderId)
                .orElseThrow(() -> new InvalidEntityException("Cylinder with ID " + cylinderId + " not found."));
    }

    public List<Cylinder> getAllCylinders(){
        return cylinderRepository.findAll();
    }

    public void deleteCylinderById(Integer cylinderId) throws InvalidEntityException {
        Cylinder cylinder = getCylinderById(cylinderId);
        cylinderRepository.delete(cylinder);
    }

    public List<Cylinder> filterCylinders(Integer id, String type, String status, String query) {
        return cylinderRepository.findAll().stream()
                // Filter by ID if a valid ID is provided (id != null)
                .filter(cylinder -> id == null || cylinder.getId() == id)
                // Filter by type if the type is not "all"
                .filter(cylinder -> "all".equalsIgnoreCase(type) || cylinder.getType().equalsIgnoreCase(type))
                // Filter by status if the status is not "all"
                .filter(cylinder -> "all".equalsIgnoreCase(status) || cylinder.getStatus().equalsIgnoreCase(status))
                // Filter by query if provided (query can still be a string search)
                .filter(cylinder -> query == null || query.isEmpty() || cylinder.toString().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    
    public void refillCylinder(Integer id) throws Exception {
        // Fetch the cylinder by ID
        Cylinder cylinder = cylinderRepository.findById(id)
            .orElseThrow(() -> new Exception("Cylinder not found"+id));

        // Update the type and status of the cylinder
        if ("empty".equalsIgnoreCase(cylinder.getType()) && "available".equalsIgnoreCase(cylinder.getStatus())) {
            cylinder.setType("full");
            cylinderRepository.save(cylinder);
        } else {
            throw new Exception("Cylinder is not empty or not available");
        }
    }


}
