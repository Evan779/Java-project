package dao;

import db.DBConnection;
import models.Category;

import java.sql.*;

public class CategoryDAO {
    public Category getById(int id) throws SQLException {
        String sql = "SELECT * FROM category_master WHERE category_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    Category cat = new Category();
                    cat.categoryId = rs.getInt("category_id");
                    cat.categoryName = rs.getString("category_name");
                    cat.gstPercent = rs.getDouble("gst_percent");
                    return cat;
                }
            }
        }
        return null;
    }
}

