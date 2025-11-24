package dao;

import db.DBConnection;
import models.User;

import java.sql.*;

public class UserDAO {

    public boolean register(String username, String password, String phone, String address) {
        String sql = "INSERT INTO users(username, password, phone, address) VALUES (?, ?, ?, ?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, phone);
            ps.setString(4, address);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.userId = rs.getInt("user_id");
                u.username = rs.getString("username");
                u.phone = rs.getString("phone");
                u.address = rs.getString("address");
                return u;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
