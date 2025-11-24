package models;

public class CartItem {
    public int id;
    public int cartId;
    public FoodItem menuItem;
    public int quantity;

    public double getTotalPrice() {
        return menuItem.basePrice * quantity;
    }

    @Override
    public String toString() {
        return menuItem.itemName + " x" + quantity + " = ₹" + String.format("%.2f", getTotalPrice());
    }
}

