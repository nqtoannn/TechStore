package com.example.hairsalon.model;

public class ResponseDataBrand {
    private String status;
    private String message;
    private BrandData data;

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

    public BrandData getData() {
        return data;
    }

    public void setData(BrandData data) {
        this.data = data;
    }
}
