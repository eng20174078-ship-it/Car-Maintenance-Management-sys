package models;

import java.time.LocalDateTime;

public class Invoice implements Billable {

    private int id;
    private MaintenanceOrder order;
    private LocalDateTime issueDate;

    public Invoice(int id, MaintenanceOrder order) {
        this.id = id;
        this.order = order;
        this.issueDate = LocalDateTime.now();
    }

    public int getId() { 
        return id; 
    }

    public MaintenanceOrder getOrder() { 
        return order; 
    }

    public LocalDateTime getIssueDate() { 
        return issueDate; 
    }

    @Override
    public double calculateTotal() {
        return order.calculateTotalCost();
    }
}
