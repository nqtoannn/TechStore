package com.quoctoan.shoestore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyRevenue {
    private int year;
    private int month;
    private Double totalRevenue;
}