package com.example.hairsalon.model;

public class Category {
    Integer id;
    String categoryName;

    public Category(Integer id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public Category() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
