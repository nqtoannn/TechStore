package com.example.hairsalon.activity.order;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.hairsalon.adapter.OrderHistoryAdapter;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.databinding.ActivityOrderHistoryBinding;
import com.example.hairsalon.model.Order;
import com.example.hairsalon.model.OrderItem;
import com.example.hairsalon.model.ResponseData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    ActivityOrderHistoryBinding binding;
    OrderHistoryAdapter orderHistoryAdapter;
    ArrayList<Order> dataArrayList = new ArrayList<>();
    Integer customerId;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        customerId = sharedPreferences.getInt("userId", 0);
        token = "Bearer " + sharedPreferences.getString("access_token","");
        getAllOrderItemHistory();
    }

    private void getAllOrderItemHistory() {
        ApiService.apiService.getAllOrderByCustomerId(customerId, token).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    dataArrayList.clear();
                    ResponseData responseData = response.body();
                    if (responseData != null && responseData.getStatus().equals("OK")) {
                        List<Map<String, Object>> orderList = responseData.getData();
                        for (Map<String, Object> order : orderList) {
                            Integer id = ((Number) order.get("id")).intValue();
                            Double totalPrice = (Double) order.get("totalPrice");
                            String paymentMethod = (String) order.get("paymentMethod");
                            String orderStatus = (String) order.get("orderStatus");
                            String orderDate = (String) order.get("orderDate");
                            String address = (String) order.get("address");
                            String note = (String) order.get("note");
                            List<Map<String, Object>> orderItemsList = (List<Map<String, Object>>) order.get("orderItems");
                            List<OrderItem> orderItems = new ArrayList<>();
                            for (Map<String, Object> item : orderItemsList) {
                                Integer orderItemId = ((Number) item.get("orderItemId")).intValue();
                                Double price = (Double) item.get("price");
                                String productName = (String) item.get("productName");
                                Integer quantity = ((Number) item.get("quantity")).intValue();
                                String productItemUrl = (String) item.get("productItemUrl");
                                Integer productItemId = ((Number) item.get("productItemId")).intValue();
                                String productItemName = (String) item.get("productItemName");
                                OrderItem orderItem = new OrderItem(orderItemId, price, quantity, productItemId, productItemUrl, productItemName, productName);
                                orderItems.add(orderItem);
                            }
                            Order orderData = new Order(id, orderItems, totalPrice, paymentMethod, orderStatus, orderDate, address, note);
                            dataArrayList.add(orderData);
                        }
                        Collections.reverse(dataArrayList);
                        orderHistoryAdapter = new OrderHistoryAdapter(OrderHistoryActivity.this, dataArrayList);
                        binding.listViewOrderHistory.setAdapter(orderHistoryAdapter);
                    } else {
                        Log.e("Error", "No order data found in response");
                    }
                } else {
                    Log.e("Error", "API call failed with error code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.e("Error", "API call failed: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllOrderItemHistory();
    }
}
