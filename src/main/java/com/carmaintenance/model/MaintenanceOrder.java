package com.carmaintenance.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceOrder {
    private int id;
    private String vehiclePlate;
    private Vehicle vehicle;  // For easy access
    private int technicianId;
    private Technician technician;  // For easy access
    private String description;
    private String status;  // Pending, In Progress, Waiting for Parts, Completed, Cancelled
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String customerNotes;
    private String internalNotes;
    private double estimatedCost;
    private double actualCost;

    // List of spare parts used in this order
    private List<OrderPart> usedParts;

    // Enums for status
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_WAITING_PARTS = "Waiting for Parts";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";

    // Constructors
    public MaintenanceOrder() {
        this.status = STATUS_PENDING;
        this.createdAt = LocalDateTime.now();
        this.usedParts = new ArrayList<>();
        this.estimatedCost = 0.0;
        this.actualCost = 0.0;
    }

    public MaintenanceOrder(String vehiclePlate, int technicianId, String description) {
        this();
        this.vehiclePlate = vehiclePlate;
        this.technicianId = technicianId;
        this.description = description;
    }

    public MaintenanceOrder(int id, String vehiclePlate, int technicianId,
                            String description, String status,
                            LocalDateTime createdAt, LocalDateTime completedAt,
                            String customerNotes, String internalNotes,
                            double estimatedCost, double actualCost) {
        this.id = id;
        this.vehiclePlate = vehiclePlate;
        this.technicianId = technicianId;
        this.description = description;
        this.status = status != null ? status : STATUS_PENDING;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.completedAt = completedAt;
        this.customerNotes = customerNotes;
        this.internalNotes = internalNotes;
        this.estimatedCost = estimatedCost;
        this.actualCost = actualCost;
        this.usedParts = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        if (vehicle != null) {
            this.vehiclePlate = vehicle.getPlateNumber();
        }
    }

    public int getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(int technicianId) {
        this.technicianId = technicianId;
    }

    public Technician getTechnician() {
        return technician;
    }

    public void setTechnician(Technician technician) {
        this.technician = technician;
        if (technician != null) {
            this.technicianId = technician.getId();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        if (STATUS_COMPLETED.equals(status) && completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getCustomerNotes() {
        return customerNotes;
    }

    public void setCustomerNotes(String customerNotes) {
        this.customerNotes = customerNotes;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public double getActualCost() {
        return actualCost;
    }

    public void setActualCost(double actualCost) {
        this.actualCost = actualCost;
    }

    public List<OrderPart> getUsedParts() {
        return usedParts;
    }

    public void setUsedParts(List<OrderPart> usedParts) {
        this.usedParts = usedParts;
    }

    // Helper methods
    public void addUsedPart(SparePart part, int quantity) {
        this.usedParts.add(new OrderPart(this.id, part.getId(), quantity, part.getPrice()));
        updateCosts();
    }

    public void removeUsedPart(int partId) {
        usedParts.removeIf(op -> op.getPartId() == partId);
        updateCosts();
    }

    public void updateCosts() {
        // Calculate actual cost based on used parts
        double partsCost = 0.0;
        for (OrderPart orderPart : usedParts) {
            partsCost += orderPart.getTotalCost();
        }
        this.actualCost = partsCost;

        // If estimated cost is not set, use actual cost as estimate
        if (this.estimatedCost == 0.0) {
            this.estimatedCost = this.actualCost;
        }
    }

    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status);
    }

    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(status);
    }

    public boolean isActive() {
        return STATUS_PENDING.equals(status) ||
                STATUS_IN_PROGRESS.equals(status) ||
                STATUS_WAITING_PARTS.equals(status);
    }

    public long getDurationInDays() {
        if (createdAt == null) return 0;
        LocalDateTime endDate = isCompleted() && completedAt != null ? completedAt : LocalDateTime.now();
        return java.time.Duration.between(createdAt, endDate).toDays();
    }

    public double getProfit() {
        return actualCost - estimatedCost;
    }

    @Override
    public String toString() {
        return String.format("MaintenanceOrder[ID=%d, Vehicle=%s, Status=%s, TechnicianID=%d]",
                id, vehiclePlate, status, technicianId);
    }

    public String getDisplayInfo() {
        return String.format("طلب #%d - %s (%s)", id, vehiclePlate, status);
    }

    public String getDetailedInfo() {
        return String.format(
                "📋 طلب الصيانة #%d\n" +
                        "================\n" +
                        "🚗 السيارة: %s\n" +
                        "👨‍🔧 الفني: %s\n" +
                        "📝 الوصف: %s\n" +
                        "📊 الحالة: %s\n" +
                        "📅 تاريخ الإنشاء: %s\n" +
                        "✅ تاريخ الإكمال: %s\n" +
                        "💰 التكلفة المتوقعة: %.2f\n" +
                        "💵 التكلفة الفعلية: %.2f\n" +
                        "📈 الربح/الخسارة: %.2f\n" +
                        "⏳ المدة: %d يوم\n" +
                        "💬 ملاحظات العميل: %s\n" +
                        "📋 ملاحظات داخلية: %s\n" +
                        "🔩 القطع المستخدمة: %d قطعة",
                id,
                vehiclePlate,
                technician != null ? technician.getName() : "غير محدد",
                description != null ? description : "لا يوجد وصف",
                status,
                createdAt != null ? createdAt.toString() : "غير محدد",
                completedAt != null ? completedAt.toString() : "غير مكتمل",
                estimatedCost,
                actualCost,
                getProfit(),
                getDurationInDays(),
                customerNotes != null ? customerNotes : "لا يوجد",
                internalNotes != null ? internalNotes : "لا يوجد",
                usedParts.size()
        );
    }

    // Inner class for Order-Part relationship
    public static class OrderPart {
        private int orderId;
        private int partId;
        private int quantity;
        private double unitPrice;

        public OrderPart(int orderId, int partId, int quantity, double unitPrice) {
            this.orderId = orderId;
            this.partId = partId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        // Getters and Setters
        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public int getPartId() {
            return partId;
        }

        public void setPartId(int partId) {
            this.partId = partId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public double getTotalCost() {
            return quantity * unitPrice;
        }

        @Override
        public String toString() {
            return String.format("OrderPart[OrderID=%d, PartID=%d, Qty=%d, Price=%.2f]",
                    orderId, partId, quantity, unitPrice);
        }
    }
}