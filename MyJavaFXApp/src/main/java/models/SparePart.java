package models;

public class SparePart {
    private int id;
    private String name;
    private double unitPrice;
    private int stockQuantity;

    public SparePart(int id, String name, double unitPrice, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
    }

    // Getters & Setters
    public int getId() { 
        return id; 
    }

    public String getName() { 
        return name; 
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public double getUnitPrice() { 
        return unitPrice; 
    }

    public void setUnitPrice(double unitPrice) { 
        this.unitPrice = unitPrice; 
    }

    public int getStockQuantity() { 
        return stockQuantity; 
    }

    public void setStockQuantity(int stockQuantity) { 
        this.stockQuantity = stockQuantity; 
    }

    public void reduceStock(int quantity) throws InsufficientStockException {
        if (quantity > stockQuantity) {
            throw new InsufficientStockException("Not enough stock for part: " + name);
        }
        this.stockQuantity -= quantity;
    }
}
