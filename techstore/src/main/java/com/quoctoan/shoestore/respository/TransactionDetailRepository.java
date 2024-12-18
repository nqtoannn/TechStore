package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.Transaction;
import com.quoctoan.shoestore.entity.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDetailRepository extends JpaRepository<TransactionDetail, Integer> {

}
