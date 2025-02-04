package com.example.hairsalon.model;

import java.util.List;
import java.util.Map;

public class Product {
    private int id;
    private String name;
    private String description;
    private String categoryName;
    private String imageUrl;
    private String category;
    private String brand;
    private String status;
    private int rated;
    private double rating;
    private int sold;
    private String avrPrice;
    private Map<String, String> attributes;
    private List<ProductItem> productItems;
    private int discountPercent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRated() {
        return rated;
    }

    public void setRated(int rated) {
        this.rated = rated;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public String getAvrPrice() {
        return avrPrice;
    }

    public void setAvrPrice(String avrPrice) {
        this.avrPrice = avrPrice;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<ProductItem> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<ProductItem> productItems) {
        this.productItems = productItems;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}