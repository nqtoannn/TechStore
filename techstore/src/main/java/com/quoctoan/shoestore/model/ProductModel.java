package com.quoctoan.shoestore.model;

import com.quoctoan.shoestore.entity.Category;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    private Integer id;
    private String name;
    private String description;
    private String imageUrl;
    private Category category;
    private String status;
}
