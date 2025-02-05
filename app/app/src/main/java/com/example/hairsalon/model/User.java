package com.example.hairsalon.model;

public class User {

    private Integer id;
    private String userName;
    private String email;
    private String password;
    private String role;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String status;

    public User(Integer id, String userName, String email, String password, String role, String fullName, String phoneNumber, String address, String status) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = status;
    }

    public User(String userName, String password, String role, String email) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}

