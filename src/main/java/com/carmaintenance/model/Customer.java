package com.carmaintenance.model;

import java.time.LocalDateTime;

public class Customer {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime lastVisit;
    private int vehicleCount; // إضافة جديدة

    // Constructors
    public Customer() {
        this.createdAt = LocalDateTime.now();
    }

    public Customer(String name, String phone, String email, String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.createdAt = LocalDateTime.now();
    }

    public Customer(int id, String name, String phone, String email, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.createdAt = LocalDateTime.now();
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(LocalDateTime lastVisit) {
        this.lastVisit = lastVisit;
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(int vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    // Helper methods
    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }

    public boolean hasAddress() {
        return address != null && !address.trim().isEmpty();
    }

    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            return createdAt.toString();
        }
        return "غير محدد";
    }

    public boolean isActiveCustomer() {
        if (lastVisit == null) return true; // عميل جديد
        return lastVisit.isAfter(LocalDateTime.now().minusMonths(6)); // زار خلال 6 أشهر
    }

    @Override
    public String toString() {
        return String.format("Customer[ID=%d, Name=%s, Phone=%s, Email=%s, Vehicles=%d]",
                id, name, phone, email != null ? email : "None", vehicleCount);
    }

    public String getDisplayInfo() {
        return String.format("%s - %s (%d سيارة)", name, phone, vehicleCount);
    }

    public String getDetailedInfo() {
        return String.format(
                "👤 العميل: %s\n" +
                        "🆔 الرقم: %d\n" +
                        "📱 الهاتف: %s\n" +
                        "📧 البريد: %s\n" +
                        "🏠 العنوان: %s\n" +
                        "🚗 عدد السيارات: %d\n" +
                        "📅 تاريخ التسجيل: %s\n" +
                        "✅ الحالة: %s",
                name, id, phone,
                email != null ? email : "غير محدد",
                address != null ? address : "غير محدد",
                vehicleCount,
                getFormattedCreatedAt(),
                isActiveCustomer() ? "نشط" : "غير نشط"
        );
    }
}