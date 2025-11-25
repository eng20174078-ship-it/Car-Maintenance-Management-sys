package models;

interface Assignable {
    void assignOrder(MaintenanceOrder order, Technician technician);
}


interface RepairUpdatable {
    void updateRepairStatus(MaintenanceOrder order, RepairStatus newStatus);
}

interface Billable {
    double calculateTotal();
}