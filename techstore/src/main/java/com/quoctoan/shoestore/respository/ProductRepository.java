package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

    Page <Product> findAll(Pageable pageable);

    Page<Product> findByCategoryId(Integer categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE "
            + "(:categoryId IS NULL OR p.category.id = :categoryId) AND "
            + "(:brandId IS NULL OR p.brand.id = :brandId) AND "
            + "(:minRating IS NULL OR p.rating >= :minRating) "
            + "ORDER BY p.sold DESC")
    Page<Product> findByFiltersDesc(
            @Param("categoryId") Integer categoryId,
            @Param("brandId") Integer brandId,
            @Param("minRating") Double minRating,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE "
            + "(:categoryId IS NULL OR p.category.id = :categoryId) AND "
            + "(:brandId IS NULL OR p.brand.id = :brandId) AND "
            + "(:minRating IS NULL OR p.rating >= :minRating)")
    Page<Product> findByFilters(
            @Param("categoryId") Integer categoryId,
            @Param("brandId") Integer brandId,
            @Param("minRating") Double minRating,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE "
            + "(:categoryId IS NULL OR p.category.id = :categoryId) AND "
            + "(:brandId IS NULL OR p.brand.id = :brandId) AND "
            + "(:minRating IS NULL OR p.rating >= :minRating) "
            + "ORDER BY p.sold ASC")
    Page<Product> findByFiltersAsc(
            @Param("categoryId") Integer categoryId,
            @Param("brandId") Integer brandId,
            @Param("minRating") Double minRating,
            Pageable pageable);

}