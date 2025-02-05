package com.example.hairsalon.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String productName;
    private String productItemName;
    private int quantity;
    private String imageUrl;
    private double price;
    private boolean isSelected;
    private transient CheckedListener checkedListener;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setCheckedListener(CheckedListener listener) {
        this.checkedListener = listener;
    }

    public CheckedListener getCheckedListener() {
        return checkedListener;
    }

    public interface CheckedListener {
        void onCheckedChanged(boolean isChecked);
    }

    public CartItem() {
    }

    public CartItem(Integer id, String productName, String productItemName, int quantity, String imageUrl, double price) {
        this.id = id;
        this.productName = productName;
        this.productItemName = productItemName;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductItemName() {
        return productItemName;
    }

    public void setProductItemName(String productItemName) {
        this.productItemName = productItemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
