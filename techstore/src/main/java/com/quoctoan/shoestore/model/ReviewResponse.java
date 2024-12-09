package com.quoctoan.shoestore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewResponse {
    private Integer id;
    private Integer ratingValue;
    private Integer productId;
    private String productName;
    private String comment;
    private String imageUrl;
    private String customer;
    private String responses;
    private String createAt;
    private String status;
}
