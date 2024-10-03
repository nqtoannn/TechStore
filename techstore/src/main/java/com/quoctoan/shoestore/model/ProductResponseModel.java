package com.quoctoan.shoestore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseModel{
    private Integer id;
    private String name;
    private String description;
    private String categoryName;
    private String imageUrl;
    private String category;
    private String status;
    private String avrPrice;
    private List<ProductItemModel> productItems;
    private Integer discountPercent = 0;



    public void calculateAvrPrice() {
        if (productItems == null || productItems.isEmpty()) {
            avrPrice = "N/A";
            return;
        }

        List<Double> prices = productItems.stream()
                .map(ProductItemModel::getPrice)
                .sorted()
                .collect(Collectors.toList());

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        if (discountPercent > 0) {
            if (prices.size() >= 2) {
                double minPrice = prices.get(0) * (1 - discountPercent / 100);
                double maxPrice = prices.get(prices.size() - 1) * (1 - discountPercent / 100);
                avrPrice = formatter.format(minPrice) + " ~ " + formatter.format(maxPrice);
            } else if (prices.size() == 1) {
                avrPrice = formatter.format(prices.get(0) * (1 - discountPercent / 100));
            } else {
                avrPrice = "N/A";
            }
        } else {
            if (prices.size() >= 2) {
                double minPrice = prices.get(0);
                double maxPrice = prices.get(prices.size() - 1);
                avrPrice = formatter.format(minPrice) + " ~ " + formatter.format(maxPrice);
            } else if (prices.size() == 1) {
                avrPrice = formatter.format(prices.get(0));
            } else {
                avrPrice = "N/A";
            }
        }
    }


    public static String fmt(double d)
    {
        if(d == Math.floor(d))
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent != null ? discountPercent : 0;
    }

}
