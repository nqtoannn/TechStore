package com.quoctoan.shoestore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItemModel {
    private Integer orderItemId;
    private Double price;
    private Integer quantity;
    private Integer productItemId;
    private String productItemUrl;
    private String productItemName;
    private String productName;
}
