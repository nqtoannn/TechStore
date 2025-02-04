package com.example.hairsalon.model;

import java.util.List;
import java.util.Map;

public class ResponseData {
    private String status;
    private String message;
    private List<Map<String, Object>> data;

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

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

}
