package com.example.relaxtodrinking;

import java.util.Date;

public class Product {
    private String pro_id;
    private String pro_kind_id;
    private String pro_kind_name;
    private String pro_name;
    private int pro_price_M;
    private int pro_price_L;
    private String pro_picture; //商品圖片在Storage的完整路徑
    private int pro_status; //商品狀態 0=已下架 1=上架
    private Date pro_time;//商品新增時間

    public Product(){}

    public Product(String pro_id, String pro_kind_id, String pro_kind_name, String pro_name, int pro_price_M, int pro_price_L, String pro_picture, int pro_status, String pro_empid, Date pro_time) {
        this.pro_id = pro_id;
        this.pro_kind_id = pro_kind_id;
        this.pro_kind_name = pro_kind_name;
        this.pro_name = pro_name;
        this.pro_price_M = pro_price_M;
        this.pro_price_L = pro_price_L;
        this.pro_picture = pro_picture;
        this.pro_status = pro_status;
        this.pro_empid = pro_empid;
        this.pro_time = pro_time;
    }

    private String pro_empid; //新增者的員工id

    public String getPro_id() {
        return pro_id;
    }

    public void setPro_id(String pro_id) {
        this.pro_id = pro_id;
    }

    public String getPro_kind_id() {
        return pro_kind_id;
    }

    public void setPro_kind_id(String pro_kind_id) {
        this.pro_kind_id = pro_kind_id;
    }

    public String getPro_kind_name() {
        return pro_kind_name;
    }

    public void setPro_kind_name(String pro_kind_name) {
        this.pro_kind_name = pro_kind_name;
    }

    public String getPro_name() {
        return pro_name;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public int getPro_price_M() {
        return pro_price_M;
    }

    public void setPro_price_M(int pro_price_M) {
        this.pro_price_M = pro_price_M;
    }

    public int getPro_price_L() {
        return pro_price_L;
    }

    public void setPro_price_L(int pro_price_L) {
        this.pro_price_L = pro_price_L;
    }

    public String getPro_picture() {
        return pro_picture;
    }

    public void setPro_picture(String pro_picture) {
        this.pro_picture = pro_picture;
    }

    public int getPro_status() {
        return pro_status;
    }

    public void setPro_status(int pro_status) {
        this.pro_status = pro_status;
    }

    public String getPro_empid() {
        return pro_empid;
    }

    public void setPro_empid(String pro_empid) {
        this.pro_empid = pro_empid;
    }

    public Date getPro_time() {
        return pro_time;
    }

    public void setPro_time(Date pro_time) {
        this.pro_time = pro_time;
    }
}

