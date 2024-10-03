package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.Address;
import com.quoctoan.shoestore.entity.Cart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    @Query("SELECT A FROM Address A WHERE A.customer.id= :customerId")
    List<Address> findAllAddressByCustomerId(Integer customerId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Address c WHERE c.customer.id = :customerId")
    void deleteAllAddressByCustomerId(@Param("customerId") Integer customerId);

}
