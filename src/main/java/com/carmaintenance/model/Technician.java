package com.carmaintenance.model;

import java.time.LocalDate;

public class Technician {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String specialization;
    private LocalDate hireDate;
    private double salary;
    private String address;
    private String status; // Active, On Leave, Terminated

    // Constructors
    public Technician() {
        this.status = "Active";
    }

    public Technician(String name, String phone, String specialization) {
        this.name = name;
        this.phone = phone;
        this.specialization = specialization;
        this.status = "Active";
        this.hireDate = LocalDate.now();
    }

    public Technician(int id, String name, String phone, String email,
                      String specialization, LocalDate hireDate,
                      double salary, String address, String status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.specialization = specialization;
        this.hireDate = hireDate;
        this.salary = salary;
        this.address = address;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Helper methods
    public int getExperienceYears() {
        if (hireDate == null) return 0;
        return LocalDate.now().getYear() - hireDate.getYear();
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return String.format("Technician[ID=%d, Name=%s, Specialization=%s, Status=%s]",
                id, name, specialization, status);
    }

    public String getDisplayInfo() {
        return String.format("%s - %s (%s)", name, specialization, status);
    }

    public String getDetailedInfo() {
        return String.format(
                "👨‍🔧 الفني: %s\n" +
                        "📱 الهاتف: %s\n" +
                        "📧 البريد: %s\n" +
                        "🔧 التخصص: %s\n" +
                        "📅 تاريخ التوظيف: %s\n" +
                        "💰 الراتب: %.2f\n" +
                        "📍 العنوان: %s\n" +
                        "📊 الحالة: %s\n" +
                        "📈 سنوات الخبرة: %d",
                name, phone, email != null ? email : "غير محدد",
                specialization, hireDate != null ? hireDate.toString() : "غير محدد",
                salary, address != null ? address : "غير محدد", status, getExperienceYears()
        );
    }
}