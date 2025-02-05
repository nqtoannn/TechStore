package com.example.hairsalon.activity.order;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.hairsalon.activity.home.HomeCustomer;
import com.example.hairsalon.activity.shop.HomeShopActivity;
import com.example.hairsalon.databinding.ActivitySuccessPayBinding;

public class SuccessPay extends AppCompatActivity {

    ActivitySuccessPayBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuccessPayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessPay.this, HomeCustomer.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String url = intent.getStringExtra("payment_Url");
        if (url == null || url.isEmpty()){
            binding.btnGoToHistoryOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SuccessPay.this, OrderHistoryActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            binding.textSuccessMessage.setText("Bạn đã đặt hàng thành công. Vui lòng thanh toán đơn hàng!");
            binding.btnGoToHistoryOrder.setText("Thanh toán");
            binding.btnGoToHistoryOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   openUrlInDefaultBrowser(url);
                }
            });
        }

    }

    private void openUrlInDefaultBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.android.chrome"); // Gói ứng dụng của Chrome
        try {
            startActivity(intent);
        } catch (Exception e) {
            // Nếu Chrome không được cài đặt, fallback sang trình duyệt khác
            intent.setPackage(null); // Cho phép mở bằng bất kỳ trình duyệt nào
            startActivity(intent);
        }
    }
}