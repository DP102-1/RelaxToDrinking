package com.example.relaxtodrinking.data;

public class Employee {
    private String emp_id;
    private String emp_name;
    private int emp_permission;
    private String emp_phone;
    private String emp_sex;
    private int emp_status;
    private String user_id;
    private String emp_email;
    private String emp_address;

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public int getEmp_permission() {
        return emp_permission;
    }

    public void setEmp_permission(int emp_permission) {
        this.emp_permission = emp_permission;
    }

    public String getEmp_phone() {
        return emp_phone;
    }

    public void setEmp_phone(String emp_phone) {
        this.emp_phone = emp_phone;
    }

    public String getEmp_sex() {
        return emp_sex;
    }

    public void setEmp_sex(String emp_sex) {
        this.emp_sex = emp_sex;
    }

    public int getEmp_status() {
        return emp_status;
    }

    public void setEmp_status(int emp_status) {
        this.emp_status = emp_status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmp_email() {
        return emp_email;
    }

    public void setEmp_email(String emp_email) {
        this.emp_email = emp_email;
    }

    public String getEmp_address() {
        return emp_address;
    }

    public void setEmp_address(String emp_address) {
        this.emp_address = emp_address;
    }

    public Employee() {}

    public Employee(String emp_id, String emp_name, int emp_permission, String emp_phone, String emp_sex, int emp_status, String user_id, String emp_email, String emp_address) {
        this.emp_id = emp_id;
        this.emp_name = emp_name;
        this.emp_permission = emp_permission;
        this.emp_phone = emp_phone;
        this.emp_sex = emp_sex;
        this.emp_status = emp_status;
        this.user_id = user_id;
        this.emp_email = emp_email;
        this.emp_address = emp_address;
    }
}
