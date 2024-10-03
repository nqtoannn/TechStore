package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    @Query("SELECT pd.product, pd.discountPercent " +
            "FROM Promotion p JOIN p.promotionDetails pd " +
            "WHERE p.startDate < CURRENT_DATE AND p.endDate >= CURRENT_DATE")
    List<Object[]> findActivePromotionProducts();

    @Query("SELECT pd.discountPercent FROM PromotionDetail pd " +
            "JOIN pd.promotion p " +
            "WHERE pd.product.id = :productId " +
            "AND p.startDate <= :today " +
            "AND p.endDate >= :today")
    Optional<Integer> findDiscountPercentForProduct(@Param("productId") Integer productId,
                                                    @Param("today") LocalDate today);

}
