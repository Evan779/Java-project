package models;

public class FoodItem {
    public int itemId;
    public String itemName;
    public int restaurantId;
    public int categoryId;
    public double basePrice;
    public double packagingFee;

    @Override
    public String toString() {
        return itemName + " - ₹" + String.format("%.2f", basePrice);
    }
}

