package com.example.hairsalon.model;

import java.time.LocalDateTime;

public class Review {
    private Integer id;
    private String customer;
    private String imageUrl;
    private String comment;
    private Integer ratingValue;
    private String createAt;
    private String response;

    public Review() {
    }

    public Review(Integer id, String customer, String imageUrl, String comment, Integer ratingValue, String createAt, String response) {
        this.id = id;
        this.customer = customer;
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.ratingValue = ratingValue;
        this.createAt = createAt;
        this.response = response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
