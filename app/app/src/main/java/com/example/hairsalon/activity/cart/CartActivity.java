package com.example.hairsalon.activity.cart;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.hairsalon.R;
import com.example.hairsalon.activity.home.HomeCustomer;
import com.example.hairsalon.activity.order.PayActivity;
import com.example.hairsalon.adapter.CartItemAdapter;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.databinding.ActivityCartBinding;
import com.example.hairsalon.model.CartItem;
import com.example.hairsalon.model.ResponseData;
import com.example.hairsalon.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartItemAdapter cartItemAdapter;
    private ArrayList<CartItem> dataArrayList = new ArrayList<>();
    private List<Map<String, Object>> cartItemList = new ArrayList<>();
    private Integer customerId;
    private List<CartItem> selectedItems = new ArrayList<>();
    private double totalPrice = 0.0;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedItems.clear();
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        customerId = sharedPreferences.getInt("userId", 2);
        token = sharedPreferences.getString("access_token","");
        binding.btnBack.setOnClickListener(v -> {
                    Intent intentNew = new Intent(CartActivity .this, HomeCustomer.class);
                    startActivity(intentNew);
                }
        );
        setupPayButton();
        getAllCartItem();
    }

    private void setupPayButton() {
        binding.payButton.setOnClickListener(v -> {
            if (dataArrayList.isEmpty()) {
                Toast.makeText(CartActivity.this, "Bạn chưa có sản phẩm nào trong giỏ hàng", Toast.LENGTH_SHORT).show();
            } else {
                selectedItems.forEach(item -> item.setCheckedListener(null));
                Intent intent = new Intent(CartActivity.this, PayActivity.class);
                intent.putExtra("selectedItems", (Serializable) selectedItems);
                startActivity(intent);
            }
        });
    }

    private void getAllCartItem() {

        token = "Bearer " + token;
        ApiService.apiService.getAllCartItemsByCartId(customerId, token)
                .enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        if (response.isSuccessful()) {
                            processCartItems(response.body());
                        } else {
                            Log.e("Error", "API call failed with error code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        Log.e("Error", "API call failed: " + t.getMessage());
                        Toast.makeText(CartActivity.this, "Failed to load cart items. Please check your connection.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void processCartItems(ResponseData responseData) {
        dataArrayList.clear();
        totalPrice = 0.0;

        if (responseData != null && "OK".equals(responseData.getStatus())) {
            cartItemList = responseData.getData();

            for (Map<String, Object> cartItem : cartItemList) {
                Integer id = ((Number) cartItem.get("id")).intValue();
                String productName = (String) cartItem.get("productName");
                Integer quantity = ((Number) cartItem.get("quantity")).intValue();
                Map<String, Object> productItemModel = (Map<String, Object>) cartItem.get("productItemModel");
                String productItemName = (String) productItemModel.get("productItemName");
                double price = (double) productItemModel.get("price");
                String imageUrl = (String) productItemModel.get("imageUrl");
                CartItem cartItemAdded = new CartItem(id, productName, productItemName, quantity, imageUrl, price);
                cartItemAdded.setCheckedListener(isChecked -> {
                    if (isChecked) {
                        selectedItems.add(cartItemAdded);
                    } else {
                        selectedItems.remove(cartItemAdded);
                    }
                    updateTotalPrice();
                });

                dataArrayList.add(cartItemAdded);
            }

            updateCartDisplay(totalPrice);
        } else {
            Log.e("Error", "No product data found in response");
        }
    }

    private double calculateTotalPrice(double price, int quantity) {
        return quantity * price;
    }

    private void updateTotalPrice() {
        totalPrice = 0.0;
        for (CartItem item : selectedItems) {
            totalPrice += calculateTotalPrice(item.getPrice(), item.getQuantity());
        }
        binding.totalPrice.setText(Utils.formatPrice(totalPrice));
    }

    private void updateCartDisplay(Double totalPrice) {
        Collections.reverse(dataArrayList);
        if (cartItemAdapter == null) {
            cartItemAdapter = new CartItemAdapter(CartActivity.this, dataArrayList);
            binding.listViewCart.setAdapter(cartItemAdapter);
        } else {
            cartItemAdapter.notifyDataSetChanged();
        }
        binding.totalPrice.setText(Utils.formatPrice(totalPrice));
        binding.payButton.setEnabled(!dataArrayList.isEmpty());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllCartItem();
    }
}
