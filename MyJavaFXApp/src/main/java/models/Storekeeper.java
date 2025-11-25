package models;

public class Storekeeper extends User {

    public Storekeeper(int id, String name, String phone) {
        super(id, name, phone);
    }

    @Override
    public String getRole() {
        return "Storekeeper";
    }

    // لاحقاً تضيف دوال لإدارة المخزون (addStock, removeStock...)
}
