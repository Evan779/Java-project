package models;

public class Restaurant {
    public int restaurantId;
    public String restaurantName;
    public String location;
    public double baseDeliveryCharge;

    @Override
    public String toString() {
        return restaurantName + " (" + location + ")";
    }
}
