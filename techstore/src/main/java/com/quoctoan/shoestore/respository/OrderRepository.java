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

    @Query(value = "Select OD FROM Order OD where OD.orderStatus.id = :statusId")
    List<Order> findAllByStatusId(@Param("statusId") Integer statusId);
    @Query(value = "Select OD FROM Order OD where OD.orderStatus.id = 4 AND OD.orderStatus.id = 5")
    List<Order> findSuccessOrder();

    @Query("SELECT SUM(o.totalPrice) FROM Order o " +
            "WHERE o.orderStatus.id = 5 OR o.orderStatus.id = 4 OR o.orderStatus.id = 7 ")
    Double findRevenueBetweenDates();

    @Query("SELECT YEAR(o.orderDate), MONTH(o.orderDate), SUM(o.totalPrice) " +
            "FROM Order o " +
            "WHERE (o.orderDate >= :startDate AND o.orderDate <= :endDate) AND (o.orderStatus.id = 5 OR o.orderStatus.id = 4 OR o.orderStatus.id = 7)" +
            "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate)")
    List<Object[]> findMonthlyRevenueBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT DATE(o.orderDate), SUM(o.totalPrice) " +
            "FROM Order o " +
            "WHERE (o.orderDate >= :startDate AND o.orderDate <= :endDate) AND (o.orderStatus.id = 5 OR o.orderStatus.id = 4 OR o.orderStatus.id = 7)" +
            "GROUP BY DATE(o.orderDate)")
    List<Object[]> findDailyRevenueBetweenByDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT p.id AS product_id, p.name AS product_name, " +
            "SUM(oi.quantity * oi.price) AS total_sold, SUM(oi.quantity) AS total_quantity " +
            "FROM Product p " +
            "JOIN ProductItem pi ON pi.id = p.id " +
            "JOIN OrderItem oi ON oi.productItem.id = pi.id " +
            "JOIN Order o ON oi.id = o.id " +
            "WHERE o.orderStatus.id IN (4, 5) " +
            "GROUP BY p.id, p.name " +
            "ORDER BY total_sold DESC " +
            "LIMIT 1000")
    List<Object[]> getProductSales();


}
