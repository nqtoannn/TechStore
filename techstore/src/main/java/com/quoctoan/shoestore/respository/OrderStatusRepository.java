package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {
}
