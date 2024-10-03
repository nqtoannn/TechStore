package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query(value = "Select OD FROM Order OD where OD.customer.id = :customerId")
    List<Order> findAllOrderByCustomerId(@Param("customerId") Integer customerId);

    @Query(value = "Select OD FROM Order OD where OD.orderStatus.id = :orderId")
    List<Order> findAllByStatusId(@Param("orderId") Integer orderId);

    @Query("SELECT SUM(o.totalPrice) FROM Order o " +
            "WHERE o.orderStatus.id = 5 OR o.orderStatus.id = 4 OR o.orderStatus.id = 7 ")
    Double findRevenueBetweenDates();

    @Query("SELECT YEAR(o.orderDate), MONTH(o.orderDate), SUM(o.totalPrice) " +
            "FROM Order o " +
            "WHERE (o.orderDate >= :startDate AND o.orderDate <= :endDate) AND (o.orderStatus.id = 5 OR o.orderStatus.id = 4 OR o.orderStatus.id = 7)" +
            "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate)")
    List<Object[]> findMonthlyRevenueBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


}
