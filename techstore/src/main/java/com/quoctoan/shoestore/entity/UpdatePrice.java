package com.quoctoan.shoestore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class UpdatePrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne()
    @JoinColumn(name = "product_item_id")
    private ProductItem productItem;
    private Double price;
    private LocalDateTime startDateTime;
    private String status;
}
