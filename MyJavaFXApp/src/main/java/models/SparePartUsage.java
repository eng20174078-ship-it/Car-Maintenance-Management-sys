package models;

public class SparePartUsage {
    private SparePart sparePart;
    private int quantity;

    public SparePartUsage(SparePart sparePart, int quantity) {
        this.sparePart = sparePart;
        this.quantity = quantity;
    }

    public SparePart getSparePart() {
        return sparePart;
    }

    public void setSparePart(SparePart sparePart) {
        this.sparePart = sparePart;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalCost() {
        return sparePart.getUnitPrice() * quantity;
    }
}
