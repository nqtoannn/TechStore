package com.quoctoan.shoestore.model;

import java.time.LocalDate;

public class DailyRevenue {
    private LocalDate date;
    private Double totalRevenue;

    public DailyRevenue(LocalDate date, Double totalRevenue) {
        this.date = date;
        this.totalRevenue = totalRevenue;
    }

    // Getters và Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
