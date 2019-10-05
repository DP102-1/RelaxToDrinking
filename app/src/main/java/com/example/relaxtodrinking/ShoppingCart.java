package com.example.relaxtodrinking;

public class ShoppingCart {
    private String sc_item_id;
    private String pro_id;
    private String pro_name;
    private String pro_capacity;
    private int pro_price;
    private int pro_quantity;
    private String pro_temperature;
    private String pro_sweetness;

    public String getSc_item_id() {
        return sc_item_id;
    }

    public void setSc_item_id(String sc_item_id) {
        this.sc_item_id = sc_item_id;
    }

    public String getPro_id() {
        return pro_id;
    }

    public void setPro_id(String pro_id) {
        this.pro_id = pro_id;
    }

    public String getPro_name() {
        return pro_name;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public String getPro_capacity() {
        return pro_capacity;
    }

    public void setPro_capacity(String pro_capacity) {
        this.pro_capacity = pro_capacity;
    }

    public int getPro_price() {
        return pro_price;
    }

    public void setPro_price(int pro_price) {
        this.pro_price = pro_price;
    }

    public int getPro_quantity() {
        return pro_quantity;
    }

    public void setPro_quantity(int pro_quantity) {
        this.pro_quantity = pro_quantity;
    }

    public String getPro_temperature() {
        return pro_temperature;
    }

    public void setPro_temperature(String pro_temperature) {
        this.pro_temperature = pro_temperature;
    }

    public String getPro_sweetness() {
        return pro_sweetness;
    }

    public void setPro_sweetness(String pro_sweetness) {
        this.pro_sweetness = pro_sweetness;
    }

    public ShoppingCart() {}

    public ShoppingCart(String sc_item_id, String pro_id, String pro_name, String pro_capacity, int pro_price, int pro_quantity, String pro_temperature, String pro_sweetness) {
        this.sc_item_id = sc_item_id;
        this.pro_id = pro_id;
        this.pro_name = pro_name;
        this.pro_capacity = pro_capacity;
        this.pro_price = pro_price;
        this.pro_quantity = pro_quantity;
        this.pro_temperature = pro_temperature;
        this.pro_sweetness = pro_sweetness;
    }
}
