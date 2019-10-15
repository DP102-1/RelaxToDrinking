package com.example.relaxtodrinking;

import java.util.Date;

public class Order {
    private String order_id;
    private String emp_id;
    private String user_id;
    private String user_name;
    private String user_phone;
    private String user_address;
    private int order_price;
    private int order_status;
    private Date order_date;
    private Date order_take_meal_time;

    public Order() {
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public int getOrder_price() {
        return order_price;
    }

    public void setOrder_price(int order_price) {
        this.order_price = order_price;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public Date getOrder_take_meal_time() {
        return order_take_meal_time;
    }

    public void setOrder_take_meal_time(Date order_take_meal_time) {
        this.order_take_meal_time = order_take_meal_time;
    }

    public Order(String order_id, String emp_id, String user_id, String user_name, String user_phone, String user_address, int order_price, int order_status, Date order_date, Date order_take_meal_time) {
        this.order_id = order_id;
        this.emp_id = emp_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.user_address = user_address;
        this.order_price = order_price;
        this.order_status = order_status;
        this.order_date = order_date;
        this.order_take_meal_time = order_take_meal_time;
    }
}
