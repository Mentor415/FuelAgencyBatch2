package com.faos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.faos.model.Cylinder;

@Repository
public interface CylinderRepository extends JpaRepository<Cylinder, Integer> {
    List<Cylinder> findByTypeAndStatus(String type, String status);
    Optional<Cylinder> findFirstByStatusAndTypeOrderById(String status, String type);
}