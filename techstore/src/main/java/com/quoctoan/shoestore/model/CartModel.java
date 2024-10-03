package com.quoctoan.shoestore.model;

import com.quoctoan.shoestore.entity.ProductItem;
import com.quoctoan.shoestore.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
public class CartModel {
    private Integer id;
    private String productName;
    private Integer productId;
    private ProductItemModel productItemModel;
    private Integer quantity;
}
