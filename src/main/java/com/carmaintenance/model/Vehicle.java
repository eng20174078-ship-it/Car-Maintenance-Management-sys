package com.carmaintenance.model;

public class Vehicle {
    private String plateNumber;  // Primary Key
    private String model;
    private int year;
    private int ownerId;         // Foreign Key to Customer
    private Customer owner;      // For convenience
    private String color;
    private String engineType;
    private String notes;

    // Constructors
    public Vehicle() {
    }

    public Vehicle(String plateNumber, String model, int year, int ownerId) {
        this.plateNumber = plateNumber;
        this.model = model;
        this.year = year;
        this.ownerId = ownerId;
    }

    public Vehicle(String plateNumber, String model, int year, int ownerId,
                   String color, String engineType, String notes) {
        this.plateNumber = plateNumber;
        this.model = model;
        this.year = year;
        this.ownerId = ownerId;
        this.color = color;
        this.engineType = engineType;
        this.notes = notes;
    }

    // Getters and Setters
    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
        if (owner != null) {
            this.ownerId = owner.getId();
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return String.format("Vehicle[Plate=%s, Model=%s, Year=%d, OwnerID=%d]",
                plateNumber, model, year, ownerId);
    }

    public String getDisplayInfo() {
        return String.format("%s - %s (%d)", plateNumber, model, year);
    }
}