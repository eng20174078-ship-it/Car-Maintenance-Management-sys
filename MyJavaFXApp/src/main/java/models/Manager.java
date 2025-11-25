package models;

public class Manager extends User implements Assignable {

    public Manager(int id, String name, String phone) {
        super(id, name, phone);
    }

    @Override
    public String getRole() {
        return "Manager";
    }

    @Override
    public void assignOrder(MaintenanceOrder order, Technician technician) {
        order.setAssignedTechnician(technician);
    }
}
