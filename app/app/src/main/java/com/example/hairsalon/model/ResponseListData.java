package com.example.hairsalon.model;

import java.util.List;

public class ResponseListData {
    private String status;
    private String message;
    private PaginationData data;

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

    public PaginationData getData() {
        return data;
    }

    public void setData(PaginationData data) {
        this.data = data;
    }
}
