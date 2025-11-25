package models;

public class Vehicle {
    private String licensePlate;
    private String model;
    private String ownerName;
    private String ownerPhone;

    public Vehicle(String licensePlate, String model, String ownerName, String ownerPhone) 
            throws InvalidVehicleDataException {

        if (licensePlate == null || licensePlate.isBlank()) {
            throw new InvalidVehicleDataException("License plate cannot be empty");
        }

        this.licensePlate = licensePlate;
        this.model = model;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
    }

    // Getters & Setters
    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) throws InvalidVehicleDataException {
        if (licensePlate == null || licensePlate.isBlank()) {
            throw new InvalidVehicleDataException("License plate cannot be empty");
        }
        this.licensePlate = licensePlate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) { 
        this.model = model; 
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) { 
        this.ownerName = ownerName; 
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) { 
        this.ownerPhone = ownerPhone; 
    }
}
