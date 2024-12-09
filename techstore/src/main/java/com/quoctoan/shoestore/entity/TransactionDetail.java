package com.quoctoan.shoestore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class TransactionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer trans_detail_id;
    @Column
    private int quantity;

    @Column
    private String note;

    @Column
    private Double price;

    @Column
    private Integer transaction_id;
    @Column
    private Integer product_item_id; // Thêm trường này

    @ManyToOne
    @JoinColumn(name = "product_item_id", insertable = false, updatable = false)
    private ProductItem product_item;

    @ManyToOne
    @JoinColumn(name = "transaction_id",insertable = false,updatable = false)
    @JsonIgnore
    private Transaction transaction;

}

