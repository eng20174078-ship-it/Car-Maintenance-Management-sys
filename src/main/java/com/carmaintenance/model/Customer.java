package com.carmaintenance.model;

public class Customer {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String address;

    // Constructors
    public Customer() {
    }

    public Customer(String name, String phone, String email, String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public Customer(int id, String name, String phone, String email, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
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

    @Override
    public String toString() {
        return String.format("Customer[ID=%d, Name=%s, Phone=%s]", id, name, phone);
    }

    public String toDetailedString() {
        return "معلومات العميل:\n" +
                "----------------\n" +
                "• الرقم: " + id + "\n" +
                "• الاسم: " + name + "\n" +
                "• الهاتف: " + phone + "\n" +
                "• البريد: " + (email.isEmpty() ? "غير محدد" : email) + "\n" +
                "• العنوان: " + (address.isEmpty() ? "غير محدد" : address);
    }
}