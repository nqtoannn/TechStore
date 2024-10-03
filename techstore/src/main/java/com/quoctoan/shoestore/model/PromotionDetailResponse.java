package com.quoctoan.shoestore.model;

import com.quoctoan.shoestore.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDetailResponse {
    private Integer id;
    private ProductModel product;
    private Integer discountPercent;
}
