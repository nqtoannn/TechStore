package com.example.hairsalon.model;

public class ProductItem {
    private int id;
    private String productItemName;
    private double price;
    private int quantityInStock;
    private String status;
    private String imageUrl;

    public ProductItem(int id, String productItemName, double price, int quantityInStock, String status, String imageUrl) {
        this.id = id;
        this.productItemName = productItemName;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public ProductItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductItemName() {
        return productItemName;
    }

    public void setProductItemName(String productItemName) {
        this.productItemName = productItemName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
