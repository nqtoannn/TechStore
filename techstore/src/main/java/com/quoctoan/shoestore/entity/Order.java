package com.quoctoan.shoestore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pay_id")
    private PaymentMethod paymentMethod;
    @Column(name = "total_price")
    private Double totalPrice;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_status_id")
    private OrderStatus orderStatus;
    @Column(name = "order_date")
    private LocalDate orderDate;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, orphanRemoval = true,
            cascade = CascadeType.ALL)
    private Collection<OrderItem> orderItems;
    private String address;
    private String note;
}
