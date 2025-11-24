package dao;

import db.DBConnection;
import models.Restaurant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDAO {
    public List<Restaurant> getOpenRestaurants() throws SQLException {
        List<Restaurant> list = new ArrayList<>();
        String sql = "SELECT * FROM restaurant_master ORDER BY restaurant_name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                Restaurant r = new Restaurant();
                r.restaurantId = rs.getInt("restaurant_id");
                r.restaurantName = rs.getString("restaurant_name");
                r.location = rs.getString("location");
                r.baseDeliveryCharge = rs.getDouble("base_delivery_charge");
                list.add(r);
            }
        }
        return list;
    }

    public Restaurant getById(int id) throws SQLException {
        String sql = "SELECT * FROM restaurant_master WHERE restaurant_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    Restaurant r = new Restaurant();
                    r.restaurantId = rs.getInt("restaurant_id");
                    r.restaurantName = rs.getString("restaurant_name");
                    r.location = rs.getString("location");
                    r.baseDeliveryCharge = rs.getDouble("base_delivery_charge");
                    return r;
                }
            }
        }
        return null;
    }
}
