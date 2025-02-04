package com.example.hairsalon.utils;

import java.text.DecimalFormat;

public class Utils {

    // Hàm để định dạng số tiền
    public static String formatPrice(double price) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###"); // Mẫu định dạng số tiền
        String formattedPrice = decimalFormat.format(price); // Định dạng số tiền thành chuỗi
        return formattedPrice + " VND";
    }

}
