package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.OrderStatus;
import com.quoctoan.shoestore.entity.UpdatePrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UpdatePriceRepository extends JpaRepository<UpdatePrice, Integer> {
    List<UpdatePrice> findByStartDateTimeBefore(LocalDateTime now);
}
