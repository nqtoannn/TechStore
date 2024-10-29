package com.quoctoan.shoestore.respository;

import com.quoctoan.shoestore.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

}
