package com.carmaintenance.model;

import java.time.LocalDateTime;
import java.util.List;

public class Invoice {
    private int id;
    private int orderId;
    private MaintenanceOrder order; // For easy access
    private double totalAmount;
    private double taxAmount;
    private double discountAmount;
    private double finalAmount;
    private LocalDateTime issuedDate;
    private LocalDateTime dueDate;
    private String paymentMethod; // Cash, Credit Card, Bank Transfer
    private boolean paid;
    private LocalDateTime paymentDate;
    private String notes;

    // Constructors
    public Invoice() {
        this.issuedDate = LocalDateTime.now();
        this.dueDate = LocalDateTime.now().plusDays(30); // Due in 30 days
        this.taxAmount = 0.0;
        this.discountAmount = 0.0;
        this.paid = false;
        this.paymentMethod = "Cash";
    }

    public Invoice(int orderId, double totalAmount) {
        this();
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        calculateFinalAmount();
    }

    public Invoice(int id, int orderId, double totalAmount, double taxAmount,
                   double discountAmount, LocalDateTime issuedDate, LocalDateTime dueDate,
                   String paymentMethod, boolean paid, LocalDateTime paymentDate, String notes) {
        this.id = id;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.issuedDate = issuedDate != null ? issuedDate : LocalDateTime.now();
        this.dueDate = dueDate != null ? dueDate : this.issuedDate.plusDays(30);
        this.paymentMethod = paymentMethod != null ? paymentMethod : "Cash";
        this.paid = paid;
        this.paymentDate = paymentDate;
        this.notes = notes;
        calculateFinalAmount();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public MaintenanceOrder getOrder() {
        return order;
    }

    public void setOrder(MaintenanceOrder order) {
        this.order = order;
        if (order != null) {
            this.orderId = order.getId();
            this.totalAmount = order.getActualCost();
            calculateFinalAmount();
        }
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        calculateFinalAmount();
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
        calculateFinalAmount();
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
        calculateFinalAmount();
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
        if (paid && paymentDate == null) {
            this.paymentDate = LocalDateTime.now();
        } else if (!paid) {
            this.paymentDate = null;
        }
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Helper methods
    private void calculateFinalAmount() {
        this.finalAmount = this.totalAmount + this.taxAmount - this.discountAmount;
        if (this.finalAmount < 0) {
            this.finalAmount = 0;
        }
    }

    public void applyTax(double taxPercentage) {
        this.taxAmount = this.totalAmount * (taxPercentage / 100);
        calculateFinalAmount();
    }

    public void applyDiscount(double discountPercentage) {
        this.discountAmount = this.totalAmount * (discountPercentage / 100);
        calculateFinalAmount();
    }

    public boolean isOverdue() {
        return !paid && LocalDateTime.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return java.time.Duration.between(dueDate, LocalDateTime.now()).toDays();
    }

    public String getPaymentStatus() {
        if (paid) {
            return "مدفوع";
        } else if (isOverdue()) {
            return "متأخر (" + getDaysOverdue() + " يوم)";
        } else {
            return "غير مدفوع";
        }
    }

    public String getFormattedAmounts() {
        return String.format(
                "الإجمالي: %.2f\n" +
                        "الضريبة: %.2f\n" +
                        "الخصم: %.2f\n" +
                        "المبلغ النهائي: %.2f",
                totalAmount, taxAmount, discountAmount, finalAmount
        );
    }

    public String getCustomerInfo() {
        if (order != null && order.getVehicle() != null && order.getVehicle().getOwner() != null) {
            Customer customer = order.getVehicle().getOwner();
            return String.format(
                    "العميل: %s\n" +
                            "الهاتف: %s\n" +
                            "البريد: %s\n" +
                            "العنوان: %s",
                    customer.getName(),
                    customer.getPhone(),
                    customer.getEmail() != null ? customer.getEmail() : "غير محدد",
                    customer.getAddress() != null ? customer.getAddress() : "غير محدد"
            );
        }
        return "معلومات العميل غير متوفرة";
    }

    public String getVehicleInfo() {
        if (order != null && order.getVehicle() != null) {
            Vehicle vehicle = order.getVehicle();
            return String.format(
                    "السيارة: %s\n" +
                            "الموديل: %s\n" +
                            "رقم اللوحة: %s\n" +
                            "السنة: %d",
                    vehicle.getModel(),
                    vehicle.getModel(),
                    vehicle.getPlateNumber(),
                    vehicle.getYear()
            );
        }
        return "معلومات السيارة غير متوفرة";
    }

    public String getOrderDetails() {
        if (order != null) {
            return String.format(
                    "طلب الصيانة #%d\n" +
                            "الوصف: %s\n" +
                            "التكلفة الفعلية: %.2f\n" +
                            "الفني: %s",
                    order.getId(),
                    order.getDescription(),
                    order.getActualCost(),
                    order.getTechnician() != null ? order.getTechnician().getName() : "غير محدد"
            );
        }
        return "معلومات الطلب غير متوفرة";
    }

    @Override
    public String toString() {
        return String.format("Invoice[ID=%d, OrderID=%d, Amount=%.2f, Paid=%s]",
                id, orderId, finalAmount, paid);
    }

    public String getDisplayInfo() {
        return String.format("فاتورة #%d - المبلغ: %.2f - الحالة: %s",
                id, finalAmount, getPaymentStatus());
    }

    public String getDetailedInfo() {
        return String.format(
                "🧾 الفاتورة #%d\n" +
                        "=============\n" +
                        "📋 طلب الصيانة: #%d\n" +
                        "💰 المبلغ الإجمالي: %.2f\n" +
                        "💸 الضريبة: %.2f\n" +
                        "🎁 الخصم: %.2f\n" +
                        "💵 المبلغ النهائي: %.2f\n" +
                        "📅 تاريخ الإصدار: %s\n" +
                        "⏳ تاريخ الاستحقاق: %s\n" +
                        "💳 طريقة الدفع: %s\n" +
                        "✅ حالة الدفع: %s\n" +
                        "📅 تاريخ الدفع: %s\n" +
                        "📝 ملاحظات: %s\n" +
                        "🚗 %s\n" +
                        "👤 %s",
                id, orderId, totalAmount, taxAmount, discountAmount, finalAmount,
                issuedDate != null ? issuedDate.toString() : "غير محدد",
                dueDate != null ? dueDate.toString() : "غير محدد",
                paymentMethod, getPaymentStatus(),
                paymentDate != null ? paymentDate.toString() : "غير مدفوع",
                notes != null ? notes : "لا يوجد",
                getVehicleInfo().replace("\n", "\n   "),
                getCustomerInfo().replace("\n", "\n   ")
        );
    }
}