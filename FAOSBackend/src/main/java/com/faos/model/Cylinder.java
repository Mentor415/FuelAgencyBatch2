package com.faos.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
@Table(name = "cylinders")
public class Cylinder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Integer id;

    @NotNull(message = "Cylinder status cannot be null.")
    @Pattern(regexp = "(?i)available|delivered", message = "Cylinder status must be 'available' or 'delivered'.")
    @Column(nullable = false)
    private String status; // available / delivered

    @NotNull(message = "Cylinder type cannot be null.")
    @Pattern(regexp = "(?i)full|empty", message = "Cylinder type must be 'full' or 'empty'.")
    @Column(nullable = false)
    private String type; // full / empty

    @Min(value = 1, message = "Weight must be greater than 0.")
    @Column(nullable = false)
    private float weight;

    @Column(name = "last_refill_date", nullable = false)
    private LocalDate lastRefillDate;
   

    public Cylinder() {}

    public Cylinder(Integer id, String status, String type, float weight, LocalDate lastRefillDate) {
        this.id = id;
        this.status = status;
        this.type = type;
        this.weight = weight;
        this.lastRefillDate = lastRefillDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public LocalDate getLastRefillDate() {
        return lastRefillDate;
    }

    public void setLastRefillDate(LocalDate lastRefillDate) {
        this.lastRefillDate = lastRefillDate;
    }

    @Override
    public String toString() {
        return "Cylinder{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", weight=" + weight +
                ", lastRefillDate=" + lastRefillDate +
                '}';
    }
}
    
