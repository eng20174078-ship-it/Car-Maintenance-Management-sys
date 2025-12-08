package com.carmaintenance.model;

public class SparePart {
    private int id;
    private String name;
    private String description;
    private String category;
    private String brand;
    private String partNumber;
    private double price;
    private int quantity;
    private int minThreshold;
    private String location; // مكان التخزين

    // Constructors
    public SparePart() {
        this.minThreshold = 5;
        this.quantity = 0;
    }

    public SparePart(String name, String description, String category, double price, int quantity) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.minThreshold = 5;
    }

    public SparePart(int id, String name, String description, String category,
                     String brand, String partNumber, double price,
                     int quantity, int minThreshold, String location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.brand = brand;
        this.partNumber = partNumber;
        this.price = price;
        this.quantity = quantity;
        this.minThreshold = minThreshold;
        this.location = location;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(int minThreshold) {
        this.minThreshold = minThreshold;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Helper methods
    public boolean isLowStock() {
        return quantity <= minThreshold;
    }

    public boolean isOutOfStock() {
        return quantity <= 0;
    }

    public double getTotalValue() {
        return price * quantity;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
        } else {
            this.quantity = 0;
        }
    }

    @Override
    public String toString() {
        return String.format("SparePart[ID=%d, Name=%s, Category=%s, Quantity=%d, Price=%.2f]",
                id, name, category, quantity, price);
    }

    public String getDisplayInfo() {
        return String.format("%s - %s (%d متوفر)", name, category, quantity);
    }

    public String getDetailedInfo() {
        return String.format(
                "🔩 قطعة الغيار: %s\n" +
                        "📝 الوصف: %s\n" +
                        "🏷️ الفئة: %s\n" +
                        "🏭 الماركة: %s\n" +
                        "🔢 رقم القطعة: %s\n" +
                        "💰 السعر: %.2f\n" +
                        "📦 الكمية المتاحة: %d\n" +
                        "⚠️ الحد الأدنى: %d\n" +
                        "🏠 مكان التخزين: %s\n" +
                        "💎 القيمة الإجمالية: %.2f\n" +
                        "🚨 حالة المخزون: %s",
                name,
                description != null ? description : "لا يوجد وصف",
                category != null ? category : "غير مصنف",
                brand != null ? brand : "غير محدد",
                partNumber != null ? partNumber : "غير محدد",
                price,
                quantity,
                minThreshold,
                location != null ? location : "غير محدد",
                getTotalValue(),
                isLowStock() ? "منخفض" : (isOutOfStock() ? "نفذت" : "جيد")
        );
    }
}