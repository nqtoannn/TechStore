package com.example.hairsalon.activity.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairsalon.activity.cart.CartActivity;
import com.example.hairsalon.activity.product.ListProductSearchActivity;
import com.example.hairsalon.adapter.ProductItemAdapter;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.databinding.FragmentShopBinding;
import com.example.hairsalon.model.PaginationData;
import com.example.hairsalon.model.Product;
import com.example.hairsalon.model.ResponseData;
import com.example.hairsalon.model.ResponseListData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopFragment extends Fragment {

    private FragmentShopBinding binding;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewCheapProduct;
    private List<Map<String, Object>> productItemList;

    private List<Map<String, Object>> cartItemList = new ArrayList<>();
    Integer customerId;

    public ShopFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        recyclerView = binding.recyclerViewProducts;
        recyclerViewCheapProduct = binding.recyclerViewProductsCheap;

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManagerCheapProduct = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCheapProduct.setLayoutManager(layoutManagerCheapProduct);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        customerId = sharedPreferences.getInt("userId", 2);

        ApiService.apiService.getProduct().enqueue(new Callback<ResponseListData>() {
            @Override
            public void onResponse(Call<ResponseListData> call, Response<ResponseListData> response) {
                if (response.isSuccessful()) {
                    ResponseListData responseData = response.body();
                    if (responseData != null && "OK".equals(responseData.getStatus())) {
                        PaginationData paginationData = responseData.getData();
                        if (paginationData != null) {
                            List<Product> productList = paginationData.getProducts();
                            if (productList != null) {
                                ProductItemAdapter adapter = new ProductItemAdapter(productList);
                                recyclerView.setAdapter(adapter);
                                List<Product> productListReverse = new ArrayList<>(productList);
                                Collections.reverse(productListReverse);
                                ProductItemAdapter cheapAdapter = new ProductItemAdapter(productListReverse);
                                recyclerViewCheapProduct.setAdapter(cheapAdapter);
                            } else {
                                Log.e("Error", "No products found in pagination data");
                            }
                        } else {
                            Log.e("Error", "No pagination data found in response");
                        }
                    } else {
                        Log.e("Error", "Invalid response status or no data");
                    }
                } else {
                    Log.e("Error", "API call failed with error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseListData> call, Throwable t) {
                Log.e("Error", "API call failed: " + t.getMessage());
            }
        });


        binding.cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        binding.searchBar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (binding.searchBar.getRight() - binding.searchBar.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        String searchQuery = binding.searchBar.getText().toString();
                        @SuppressLint("ClickableViewAccessibility") Intent intent = new Intent(requireContext(), ListProductSearchActivity.class);
                        intent.putExtra("searchQuery", searchQuery);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
