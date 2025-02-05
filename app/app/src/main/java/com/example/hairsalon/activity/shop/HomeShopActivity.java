package com.example.hairsalon.activity.shop;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalon.R;
import com.example.hairsalon.activity.cart.CartActivity;
import com.example.hairsalon.activity.product.ListProductSearchActivity;
import com.example.hairsalon.adapter.ProductItemAdapter;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.databinding.ActivityHomeShopBinding;
import com.example.hairsalon.model.ResponseData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeShopActivity extends AppCompatActivity {

    ActivityHomeShopBinding binding;


    RecyclerView recyclerView;

    RecyclerView recyclerViewCheapProduct;
    List<Map<String, Object>> productItemList;

    Integer userId;
    String token;

    private List<Map<String, Object>> cartItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        token = sharedPreferences.getString("access_token","");
        recyclerView = findViewById(R.id.recycler_view_products);
        recyclerViewCheapProduct = findViewById(R.id.recycler_view_products_cheap);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Tạo một LinearLayoutManager mới cho recyclerViewCheapProduct
        LinearLayoutManager layoutManagerCheapProduct = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCheapProduct.setLayoutManager(layoutManagerCheapProduct);
        getAllCartItemsAndUpdateCount();
//        ApiService.apiService.getProductItem().enqueue(new Callback<ResponseData>() {
//            @Override
//            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
//                if (response.isSuccessful()) {
//                    ResponseData responseData = response.body();
//                    if (responseData != null && responseData.getStatus().equals("OK")) {
//                        productItemList = responseData.getData();
//                        ProductItemAdapter adapter = new ProductItemAdapter(productItemList);
//                        recyclerView.setAdapter(adapter);
//                        List<Map<String, Object>> productItemListReverse = new ArrayList<>(productItemList);
//                        Collections.reverse(productItemListReverse);
//
//                        ProductItemAdapter cheapAdapter = new ProductItemAdapter(productItemListReverse);
//                        recyclerViewCheapProduct.setAdapter(cheapAdapter);
//
//                    } else {
//                        Log.e("Error", "No product data found in response");
//                    }
//                } else {
//                    Log.e("Error", "API call failed with error code: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseData> call, Throwable t) {
//                Log.e("Error", "API call failed: " + t.getMessage());
//            }
//        });

        binding.cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeShopActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

//        binding.searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    String searchQuery = binding.searchBar.getText().toString();
//                    Intent intent = new Intent(HomeShopActivity.this, ListProductSearchActivity.class);
//                    intent.putExtra("searchQuery", searchQuery);
//                    startActivity(intent);
//                    return true;
//                }
//                return false;
//            }
//        });

        binding.searchBar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (binding.searchBar.getRight() - binding.searchBar.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        String searchQuery = binding.searchBar.getText().toString();
                        @SuppressLint("ClickableViewAccessibility") Intent intent = new Intent(HomeShopActivity.this, ListProductSearchActivity.class);
                        intent.putExtra("searchQuery", searchQuery);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("BackTo", "Back");
        getAllCartItemsAndUpdateCount();
    }

    private void getAllCartItemsAndUpdateCount() {
        Log.i("Called Api", "Called");
        token = "Bearer " + token;
        ApiService.apiService.getAllCartItemsByCartId(userId,token).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, retrofit2.Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    ResponseData responseData = response.body();
                    if (responseData != null && responseData.getStatus().equals("OK")) {
                        cartItemList = responseData.getData();
                        Integer totalNumber = 0;
                        for (Map<String, Object> cartItemList : cartItemList) {
                            totalNumber +=  ((Number) cartItemList.get("quantity")).intValue();
                        }
                        updateCartCount(totalNumber);
                    } else {
                        updateCartCount(0);
                        Log.e("Error", "No cart item data found in response");
                    }
                } else {
                    updateCartCount(0);
                    Log.e("Error", "API call failed with error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                updateCartCount(0);
                Log.e("Error", "API call failed: " + t.getMessage());
            }
        });
    }



    private void updateCartCount(Integer cartItemCount) {
        Log.i("Updating", String.valueOf(cartItemCount));
        binding.textCartCount.setText(String.valueOf(cartItemCount));
    }


}
