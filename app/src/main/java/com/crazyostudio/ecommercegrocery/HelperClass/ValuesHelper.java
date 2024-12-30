package com.crazyostudio.ecommercegrocery.HelperClass;

public class ValuesHelper {

    // Order States
    public static final String PROCESSING = "Processing"; // New order placed
    public static final String CONFIRMED = "Confirmed"; // Order prepared and out for delivery
    public static final String OUTFORDELIVERY = "Out for Delivery"; // Package with delivery boy
    public static final String DELIVERED = "Delivered"; // Successfully delivered
    public static final String CANCELLED = "Cancelled"; // Cancelled by admin
    public static final String CUSTOMER_REJECTED = "Customer Rejected"; // Refused by customer
    public static final String CUSTOMER_NOT_AVAILABLE = "Customer Not Available"; // Customer unreachable

    // Order State Colors
    public static final String PROCESSING_COLOR = "#FF9800"; // Orange for Processing
    public static final String CONFIRMED_COLOR = "#03A9F4"; // Light Blue for Confirmed
    public static final String OUTFORDELIVERY_COLOR = "#FFEB3B"; // Yellow for Out for Delivery
    public static final String DELIVERED_COLOR = "#4CAF50"; // Green for Delivered
    public static final String CANCELLED_COLOR = "#F44336"; // Red for Cancelled
    public static final String CUSTOMER_REJECTED_COLOR = "#9C27B0"; // Purple for Customer Rejected
    public static final String CUSTOMER_NOT_AVAILABLE_COLOR = "#607D8B"; // Grey for Customer Not Available

    // Pricing Constants
    public static final String RupeeSymbols = "₹"; // Indian Rupee symbol
    //    public static final double MIN_TOTAL_PRICE = 120; // Minimum cart value for free delivery
//    public static final double MIN_TOTAL_PRICE_VALUE = 35; // Delivery fee if below the minimum cart value
    public static final double MIN_TOTAL_PRICE_FOR_DELIVERY = 120; // Minimum cart value for above the 120 delivery was free
    public static final double DeliveryFees = 35;
    public static final double StandardDeliveryFees = 00;
    public static final double processingFee = 00;
    public static final double MinDonate = 00;


    // User Auth System Defaults
    public static final String DEFAULT_USER_NAME = "Hi User"; // Default user name for new users
}
