package com.quoctoan.shoestore.model;

import com.quoctoan.shoestore.entity.TransactionDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseModel {
    private Integer transaction_id;
    private String createdAt;
    private Integer total_quantity;
    private Double total_price;
    private String status;
    private String type;
    private Integer userId;
    private String username;
    private List<TransactionDetailResponseModel> transactionDetails = new ArrayList<>();
}
