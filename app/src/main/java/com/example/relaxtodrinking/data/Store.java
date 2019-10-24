package com.example.relaxtodrinking.data;

public class Store {
    private String store_id;
    private String store_name;
    private String store_address;
    private String store_phone;
    private Double store_longitude;
    private Double store_latitude;
    private String store_picture;

    public String getStore_picture() {
        return store_picture;
    }
    public void setStore_picture(String store_picture) {
        this.store_picture = store_picture;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_address() {
        return store_address;
    }

    public void setStore_address(String store_address) {
        this.store_address = store_address;
    }

    public String getStore_phone() {
        return store_phone;
    }

    public void setStore_phone(String store_phone) {
        this.store_phone = store_phone;
    }

    public Double getStore_longitude() {
        return store_longitude;
    }

    public void setStore_longitude(Double store_longitude) {
        this.store_longitude = store_longitude;
    }

    public Double getStore_latitude() {
        return store_latitude;
    }

    public void setStore_latitude(Double store_latitude) {
        this.store_latitude = store_latitude;
    }

    public Store(String store_id,String store_picture, String store_name, String store_address, String store_phone, Double store_longitude, Double store_latitude) {
        this.store_id = store_id;
        this.store_name = store_name;
        this.store_address = store_address;
        this.store_phone = store_phone;
        this.store_longitude = store_longitude;
        this.store_latitude = store_latitude;
        this.store_picture = store_picture;
    }

    public Store() { }
}
