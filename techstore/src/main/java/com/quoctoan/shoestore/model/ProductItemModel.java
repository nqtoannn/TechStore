package com.quoctoan.shoestore.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quoctoan.shoestore.entity.Cart;
import com.quoctoan.shoestore.entity.OrderItem;
import com.quoctoan.shoestore.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductItemModel {
    private Integer id;
    private String productItemName;
    private Double price;
    private Integer quantityInStock;
    private String status;
    private String imageUrl;
    private String detailQuantity;
}
