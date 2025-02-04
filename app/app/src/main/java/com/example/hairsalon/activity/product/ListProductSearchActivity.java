package com.example.hairsalon.activity.product;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.hairsalon.adapter.GridAdapter;
import com.example.hairsalon.adapter.ProductItemAdapter;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.databinding.ActivityListProductBinding;
import com.example.hairsalon.model.PaginationData;
import com.example.hairsalon.model.Product;
import com.example.hairsalon.model.ProductItem;
import com.example.hairsalon.model.ResponseData;
import com.example.hairsalon.model.ResponseListData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListProductSearchActivity extends AppCompatActivity {

    ActivityListProductBinding binding;
    GridAdapter gridAdapter;
    List<Product> dataArrayList = new ArrayList<>();
    private List<Map<String, Object>> productItemList = new ArrayList<>();
    String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        searchQuery = getIntent().getStringExtra("searchQuery");
        if (searchQuery != null) {
            performSearch(searchQuery);
        } else{
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
                                    dataArrayList = productList;
                                    gridAdapter = new GridAdapter(ListProductSearchActivity.this, dataArrayList);
                                    Log.i("productItemList", dataArrayList.toString());
                                    binding.gridView.setAdapter(gridAdapter);
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
        };

        if (dataArrayList.isEmpty()) {
            binding.emptyTextView.setVisibility(View.VISIBLE);
            binding.labelName.setText("Kết quả tìm kiếm của: " + searchQuery);
        }

        binding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product clickedProduct = dataArrayList.get(i);
                Log.i("clicked", clickedProduct.toString());
                Intent intent = new Intent(ListProductSearchActivity.this, DetailProductActivity.class);
                startActivity(intent);
            }
        });
    }

    private void performSearch(String query) {
        ApiService.apiService.searchProductByName(query).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    ResponseData responseData = response.body();
                    if (responseData != null && responseData.getStatus().equals("OK")) {
                        binding.emptyTextView.setVisibility(View.GONE);
                        binding.labelName.setText("Kết quả tìm kiếm của: " + query);
                        productItemList = responseData.getData();
                        List<Product> productList = new ArrayList<>();
                        for (Map<String, Object> map : productItemList) {
                            Product product = new Product();
                            product.setId(((Number) map.get("id")).intValue());
                            product.setName((String) map.get("name"))      ;
                            product.setDescription((String) map.get("description"));
                            product.setCategoryName((String) map.get("categoryName"));
                            product.setImageUrl((String) map.get("imageUrl"));
                            product.setCategory((String) map.get("category"));
                            product.setBrand((String) map.get("brand"));
                            product.setStatus((String) map.get("status"));
                            product.setRated(((Number) map.get("rated")).intValue());
                            product.setRating((double) map.get("rating"));
                            product.setSold(((Number) map.get("sold")).intValue());
                            product.setAvrPrice((String) map.get("avrPrice"));
                            product.setAttributes((Map<String, String>) map.get("attributes"));
                            product.setProductItems((List<ProductItem>) map.get("productItems"));
                            product.setDiscountPercent(((Number) map.get("discountPercent")).intValue());
                            productList.add(product);
                        }
                        if (productList != null) {
                            GridAdapter adapter = new GridAdapter(ListProductSearchActivity.this,productList);
                            binding.gridView.setAdapter(adapter);
                        } else {
                            Log.e("Error", "No products found in pagination data");
                        }
                        gridAdapter = new GridAdapter(ListProductSearchActivity.this, dataArrayList);
                        Log.i("productItemList", dataArrayList.toString());
                        binding.gridView.setAdapter(gridAdapter);
                    } else {
                        Log.e("Error", "Unexpected status");
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
}
