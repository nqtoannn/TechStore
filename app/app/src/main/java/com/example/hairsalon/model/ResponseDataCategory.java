package com.example.hairsalon.model;

import java.util.List;

public class ResponseDataCategory {
    private String status;
    private String message;
    private CategoryData data;

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

    public CategoryData getData() {
        return data;
    }

    public void setData(CategoryData data) {
        this.data = data;
    }
}




