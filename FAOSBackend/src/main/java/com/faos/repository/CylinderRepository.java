package com.faos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.faos.model.Cylinder;

@Repository
public interface CylinderRepository extends JpaRepository<Cylinder, Integer> {
    List<Cylinder> findByTypeAndStatus(String type, String status);
}