package com.quoctoan.shoestore.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderModel {
    private Integer id;
    private List<OrderItemModel> orderItems;
    private Double totalPrice;
    private String paymentMethod;
    private String orderStatus;
    private LocalDate orderDate;
    private String address;
}
