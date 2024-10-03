package com.quoctoan.shoestore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionResponse {
    private Integer id;
    private String promotionName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<PromotionDetailResponse> promotionDetail;
}
