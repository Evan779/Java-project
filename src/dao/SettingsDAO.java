package dao;

import db.DBConnection;
import models.AppSettings;

import java.sql.*;

public class SettingsDAO {
    public AppSettings getSettings() throws SQLException {
        String sql = "SELECT * FROM app_settings LIMIT 1";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            if (rs.next()) {
                AppSettings s = new AppSettings();
                s.platformFee = rs.getDouble("platform_fee");
                s.surgePercent = rs.getDouble("surge_percent");
                s.defaultDeliveryCharge = rs.getDouble("default_delivery_charge");
                return s;
            }
        }
        return null;
    }
}
