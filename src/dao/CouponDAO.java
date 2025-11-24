package dao;

import db.DBConnection;
import models.Coupon;

import java.sql.*;

public class CouponDAO {
    public Coupon getCoupon(String code) throws SQLException {
        String sql = "SELECT * FROM coupon_master WHERE coupon_code = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, code);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    Coupon cp = new Coupon();
                    cp.couponCode = rs.getString("coupon_code");
                    cp.discountPercent = rs.getDouble("discount_percent");
                    cp.maxDiscount = rs.getDouble("max_discount");
                    cp.minimumOrder = rs.getDouble("minimum_order");
                    return cp;
                }
            }
        }
        return null;
    }
}
