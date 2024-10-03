package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail( String email);

    @Query(value = "SELECT U FROM User U where U.role = 'CUSTOMER'")
    List<User> findAllCustomer();

    @Query(value = "SELECT U FROM User U where U.role = 'EMPLOYEE'")
    List<User> findAllEmployee();

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);

}
