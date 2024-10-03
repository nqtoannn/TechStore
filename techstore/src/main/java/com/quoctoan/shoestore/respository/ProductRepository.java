package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.Product;
import com.quoctoan.shoestore.entity.ProductItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT P FROM Product P WHERE P.status = 'ACTIVE' AND P.category.id = :categoryId")
    List<Product> findByCategoryId(Integer categoryId);
    @Query(value = "SELECT S FROM Product S WHERE S.name LIKE CONCAT('%', :productName, '%')")
    List<Product> findByProductName( String productName);
    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.imageUrl = :image where p.id = :productId")
    void updateImage(String image, Integer productId);
    @Query(value = "SELECT P FROM Product P WHERE P.status = 'ACTIVE'")
    List<Product> findAllProduct();
}