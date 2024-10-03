package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.ProductItem;
import com.quoctoan.shoestore.entity.Review;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query(value = "SELECT R FROM Review R WHERE R.product.id = :productId")
    List<Review> findByProductId(@Param("productId") Integer productId);

    @Transactional
    @Modifying
    @Query("UPDATE Review p SET p.imageUrl = :image where p.id = :reviewId")
    void updateImage(String image, Integer reviewId);
}
