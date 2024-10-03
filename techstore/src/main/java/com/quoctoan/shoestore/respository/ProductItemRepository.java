package com.quoctoan.shoestore.respository;


import com.quoctoan.shoestore.entity.ProductItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ProductItemRepository extends JpaRepository<ProductItem, Integer> {

    @Query(value = "SELECT P FROM ProductItem P WHERE P.status = 'ACTIVE'")
    List<ProductItem> findAllProductItem();
    @Transactional
    @Modifying
    @Query("UPDATE ProductItem p SET p.imageUrl = :image where p.id = :productItemId")
    void updateImage(String image, Integer productItemId);
    @Query(value = "SELECT S FROM ProductItem S WHERE S.product.id = :productId")
    List<ProductItem> findByProductId(@Param("productId") Integer productId);

    @Query(value = "SELECT pi " +
            "FROM ProductItem pi " +
            "JOIN pi.orderItems oi " +
            "JOIN oi.order o " +
            "WHERE o.orderStatus.id IN (4, 5) " +
            "GROUP BY pi.id " +
            "ORDER BY SUM(oi.quantity) DESC" )
    List<ProductItem> getMostPruchased();
}
