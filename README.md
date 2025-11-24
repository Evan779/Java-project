Food Delivery System

A  Java + MySQL desktop-based food delivery application to display final price on the first page of order instead of the final checkout page.This gives customer the price of product instead of base price excluding other charges like GST,Surge,Delivery etc.
Features include authentication, restaurant listings, menu display, cart price calculation, search options and checkout option.

Features

1 User Module

Login & Signup with validation

User profile storage (name, phone, address)

2 Restaurant Module

Fetch all open restaurants

Show menu linked with restaurant ID

3 Food Menu Module

List food items per restaurant

Each food item contains price, name, restaurantId

GST depends on the item category

4 Cart & Price Calculation

Add items with quantity

Complete price breakdown:
Subtotal
GST
Delivery charge
Packaging charge
Platform fee
Surge pricing
Coupon discounts
Final Total

Database Schema (MySQL)
(https://github.com/user-attachments/assets/ae330fd9-21c7-46d5-ab6f-527dee6ed45b)


Technologies Used


Layer	Technology

Language	Java 21

Database	MySQL

Build Tool	IntelliJ IDEA

Architecture	DAO + MVC-ish layered design

How to Run

1. Clone Repository
git clone https://github.com/yourusername/JavaProject.git
cd JavaProject

2. Setup MySQL

Create a database:

CREATE DATABASE foodapp;

Import SQL tables

Update DB credentials in DBConnection.java

3. Add Images for Login Background

Place all images inside:

src/ui/images/bg/

4. Run the App

Open IntelliJ → Run Mainframe.java



