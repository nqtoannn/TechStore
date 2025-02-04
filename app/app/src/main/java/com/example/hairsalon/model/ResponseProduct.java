package com.example.hairsalon.model;

import java.util.List;
import java.util.Map;

public class ResponseProduct {
    private String status;
    private String message;
    private Product data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Product getProduct() {
        return data;
    }

    public void setProduct(Product product) {
        this.data = product;
    }
}
