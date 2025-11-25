package models;

public class Receptionist extends User {

    public Receptionist(int id, String name, String phone) {
        super(id, name, phone);
    }

    @Override
    public String getRole() {
        return "Receptionist";
    }

    // هنا لاحقاً ممكن تضيف دوال مثل: registerVehicle(), createMaintenanceOrder()
}
