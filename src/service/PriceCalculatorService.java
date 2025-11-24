package service;

import dao.*;
import models.*;

public class PriceCalculatorService {
    private FoodDAO foodDAO = new FoodDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private SettingsDAO settingsDAO = new SettingsDAO();
    private CouponDAO couponDAO = new CouponDAO();
    private dao.RestaurantDAO restaurantDAO = new dao.RestaurantDAO();

    public PriceBreakdown calculatePrice(int itemId, int quantity, String couponCode) throws Exception {
        FoodItem item = foodDAO.getFoodById(itemId);
        Category cat = categoryDAO.getById(item.categoryId);
        AppSettings settings = settingsDAO.getSettings();
        Restaurant rest = restaurantDAO.getById(item.restaurantId);

        double subtotal = item.basePrice * quantity;
        double gst = subtotal * (cat.gstPercent / 100.0);
        double packaging = item.packagingFee;
        double delivery = (rest != null) ? rest.baseDeliveryCharge : settings.defaultDeliveryCharge;
        double platform = settings.platformFee;
        double surge = subtotal * (settings.surgePercent / 100.0);

        double discount = 0;
        if (couponCode != null && !couponCode.isEmpty()) {
            Coupon cp = couponDAO.getCoupon(couponCode);
            if (cp != null && subtotal >= cp.minimumOrder) {
                discount = Math.min(subtotal * (cp.discountPercent / 100.0), cp.maxDiscount);
            }
        }

        double total = subtotal + gst + packaging + delivery + platform + surge - discount;
        PriceBreakdown pb = new PriceBreakdown();
        pb.subtotal = subtotal;
        pb.gst = gst;
        pb.packaging = packaging;
        pb.delivery = delivery;
        pb.platform = platform;
        pb.surge = surge;
        pb.discount = discount;
        pb.total = total;
        return pb;
    }

    public static class PriceBreakdown {
        public double subtotal, gst, packaging, delivery, platform, surge, discount, total;
    }
}


