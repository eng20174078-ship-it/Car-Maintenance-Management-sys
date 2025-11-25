package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceOrder {

    private int id;
    private Vehicle vehicle;
    private String serviceType;
    private RepairStatus status;
    private Technician assignedTechnician;
    private LocalDateTime createdAt;
    private double laborCost;

    private List<SparePartUsage> usedParts = new ArrayList<>();

    public MaintenanceOrder(int id, Vehicle vehicle, String serviceType) {
        this.id = id;
        this.vehicle = vehicle;
        this.serviceType = serviceType;
        this.status = RepairStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public int getId() { 
        return id; 
    }

    public Vehicle getVehicle() { 
        return vehicle; 
    }

    public void setVehicle(Vehicle vehicle) { 
        this.vehicle = vehicle; 
    }

    public String getServiceType() { 
        return serviceType; 
    }

    public void setServiceType(String serviceType) { 
        this.serviceType = serviceType; 
    }

    public RepairStatus getStatus() { 
        return status; 
    }

    public void setStatus(RepairStatus status) { 
        this.status = status; 
    }

    public Technician getAssignedTechnician() { 
        return assignedTechnician; 
    }

    public void setAssignedTechnician(Technician assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    public double getLaborCost() { 
        return laborCost; 
    }

    public void setLaborCost(double laborCost) { 
        this.laborCost = laborCost; 
    }

    public List<SparePartUsage> getUsedParts() {
        return usedParts;
    }

    public void addUsedPart(SparePartUsage usage) {
        usedParts.add(usage);
    }

    public double calculatePartsCost() {
        return usedParts.stream()
                .mapToDouble(SparePartUsage::getTotalCost)
                .sum();
    }

    public double calculateTotalCost() {
        return calculatePartsCost() + laborCost;
    }
}
