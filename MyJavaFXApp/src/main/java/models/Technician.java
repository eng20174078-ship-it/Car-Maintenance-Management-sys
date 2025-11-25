package models;

public class Technician extends User implements RepairUpdatable {

    private String specialty;

    public Technician(int id, String name, String phone, String specialty) {
        super(id, name, phone);
        this.specialty = specialty;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
    public String getRole() {
        return "Technician";
    }

    @Override
    public void updateRepairStatus(MaintenanceOrder order, RepairStatus newStatus) {
        order.setStatus(newStatus);
    }
}
