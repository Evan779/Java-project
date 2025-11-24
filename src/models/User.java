package models;

public class User {
    public int userId;
    public String username;
    public String password;
    public String phone;
    public String address;

    @Override
    public String toString() {
        return username;
    }
}
