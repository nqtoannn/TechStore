package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.Cart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    @Query("SELECT A FROM Cart A " +
            "WHERE A.customer.id= :customerId")
    List<Cart> findAllCartByCustomerId(Integer customerId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.customer.id = :customerId")
    void deleteAllCartByCustomerId(@Param("customerId") Integer customerId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.id = :cartId")
    void deleteCartById(@Param("cartId") Integer cartId);

    @Query("SELECT c FROM Cart c WHERE c.customer.id = :customerId AND c.productItem.id = :productItemId")
    Cart findExistCart(Integer customerId, Integer productItemId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.customer.id = :customerId AND c.productItem.id = :productItemId")
    void deleteCartByProductItemId(Integer customerId, Integer productItemId);

}
