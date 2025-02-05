package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.ProductItem;
import com.quoctoan.shoestore.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query(value = "SELECT T FROM Transaction T WHERE T.type = :type")
    List<Transaction> findTransactionByType (String type);

}
