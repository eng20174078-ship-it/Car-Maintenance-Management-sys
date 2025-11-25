package models;

public abstract class User {
    private int id;
    private String name;
    private String phone;

    public User(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    // Encapsulation: Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) { 
        this.id = id; 
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) { 
        this.phone = phone; 
    }

    // Polymorphism: كل Subclass يحدد دوره
    public abstract String getRole();
}
