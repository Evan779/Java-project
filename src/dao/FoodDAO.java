package dao;

import db.DBConnection;
import models.FoodItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodDAO {
    public List<FoodItem> getMenuByRestaurant(int restaurantId) throws SQLException {
        List<FoodItem> items = new ArrayList<>();
        String sql = "SELECT * FROM food_master WHERE restaurant_id = ? AND base_price IS NOT NULL ORDER BY item_name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, restaurantId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    FoodItem f = new FoodItem();
                    f.itemId = rs.getInt("item_id");
                    f.itemName = rs.getString("item_name");
                    f.restaurantId = rs.getInt("restaurant_id");
                    f.categoryId = rs.getInt("category_id");
                    f.basePrice = rs.getDouble("base_price");
                    f.packagingFee = rs.getDouble("packaging_fee");
                    items.add(f);
                }
            }
        }
        return items;
    }

    public FoodItem getFoodById(int id) throws SQLException {
        String sql = "SELECT * FROM food_master WHERE item_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    FoodItem f = new FoodItem();
                    f.itemId = rs.getInt("item_id");
                    f.itemName = rs.getString("item_name");
                    f.restaurantId = rs.getInt("restaurant_id");
                    f.categoryId = rs.getInt("category_id");
                    f.basePrice = rs.getDouble("base_price");
                    f.packagingFee = rs.getDouble("packaging_fee");
                    return f;
                }
            }
        }
        return null;
    }
}
