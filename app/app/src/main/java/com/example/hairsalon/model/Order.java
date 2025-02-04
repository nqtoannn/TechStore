package com.example.hairsalon.model;


import java.util.List;

public class Order {
    private Integer id;
    private List<OrderItem> orderItems;
    private Double totalPrice;
    private String paymentMethod;
    private String orderStatus;
    private String orderDate;
    private String address;
    private String note;

    public Order(Integer id, List<OrderItem> orderItems, Double totalPrice, String paymentMethod, String orderStatus, String orderDate, String address, String note) {
        this.id = id;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.address = address;
        this.note = note;
    }

    public Order() {
    }

    public Integer getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
