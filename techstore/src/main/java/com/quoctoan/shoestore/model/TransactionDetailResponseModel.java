package com.quoctoan.shoestore.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailResponseModel {
    private Integer trans_detail_id;
    private Integer quantity;
    private String note;
    private Double price;
    private Integer product_item_id;
    private String productItemName;
    private String productItemImg;
}
