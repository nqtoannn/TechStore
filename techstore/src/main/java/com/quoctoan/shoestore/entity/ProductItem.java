package com.quoctoan.shoestore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class ProductItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "product_item_name")
    private String productItemName;
    @Column(name = "price")
    private Double price;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @OneToMany(mappedBy = "productItem", fetch = FetchType.LAZY, orphanRemoval = true,
            cascade = CascadeType.ALL)
    private Collection<OrderItem> orderItems;
    @OneToMany(mappedBy = "productItem", fetch = FetchType.LAZY, orphanRemoval = true,
            cascade = CascadeType.ALL)
    private Collection<Cart> cartItems;
    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;
    private String detailQuantity;
    private String status;
    private String imageUrl;
}
