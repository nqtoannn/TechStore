package com.example.hairsalon.activity.product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.hairsalon.R;
import com.example.hairsalon.activity.cart.CartActivity;
import com.example.hairsalon.activity.home.HomeCustomer;
import com.example.hairsalon.activity.order.PayActivity;
import com.example.hairsalon.adapter.ImageAdapter;
import com.example.hairsalon.adapter.ReviewAdapter;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.constants.Constant;
import com.example.hairsalon.databinding.ActivityDetailProductBinding;
import com.example.hairsalon.model.Order;
import com.example.hairsalon.model.OrderItem;
import com.example.hairsalon.model.Product;
import com.example.hairsalon.model.ProductItem;
import com.example.hairsalon.model.ResponseData;
import com.example.hairsalon.model.ResponseProduct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.hairsalon.model.Review;
import com.example.hairsalon.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailProductActivity extends AppCompatActivity {

    private Integer productId, customerId;
    private List<ProductItem> productItems = new ArrayList<>();
    private ActivityDetailProductBinding binding;
    private Product product;
    private List<String> imgs = new ArrayList<>();
    private AppCompatButton selectedButton = null;
    private Integer selectedPDIId = null;
    private Double selectedPDIPrice;
    private String selectedPDIImg,selectedPDIName, token;
    private ArrayList<Review> reviewArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        customerId = sharedPreferences.getInt("userId", 2);
        token = sharedPreferences.getString("access_token","");
        Intent intent = getIntent();
        if (intent != null) {
            productId = intent.getIntExtra("productId", 2);
            fetchProductDetails();
            fetchComment();
        }
        binding.btnBack.setOnClickListener(v -> {
            Intent intentNew = new Intent(DetailProductActivity.this, HomeCustomer.class);
            startActivity(intentNew);
                }
        );
        binding.btnCart.setOnClickListener(v -> {
                    Intent intentNew = new Intent(DetailProductActivity.this, CartActivity.class);
                    startActivity(intentNew);
                }
        );
        binding.buttonAddToCart.setOnClickListener(v -> addToCart());
        binding.buttonBuyNow.setOnClickListener(v -> buyNow());
    }

    private void fetchComment(){
        ApiService.apiService.getAllComment(productId).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Map<String, Object> review :  response.body().getData()) {
                        Integer id = ((Number) review.get("id")).intValue();
                        Integer ratingValue = ((Number) review.get("ratingValue")).intValue();
                        String customer = (String) review.get("customer");
                        String imageUrl = (String) review.get("imageUrl");
                        String comment = (String) review.get("comment");
                        String createAt = (String) review.get("createAt");
                        String responseComment = (String) review.get("response");
                        Review review1 = new Review(id,customer,imageUrl,comment,ratingValue,createAt,responseComment);
                        reviewArrayList.add(review1);
                    }
                    if(reviewArrayList.size()>0){
                        RecyclerView recyclerView = findViewById(R.id.comment_part);
                        recyclerView.setLayoutManager(new LinearLayoutManager(DetailProductActivity.this));
                        binding.titleReview.setText("Đánh giá");
                        binding.commentPart.setVisibility(View.VISIBLE);
                        ReviewAdapter adapter = new ReviewAdapter(DetailProductActivity.this, reviewArrayList); // reviewList là dữ liệu
                        recyclerView.setAdapter(adapter);
                    }

                } else {
                    Log.e("Error", "API call failed with error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void fetchProductDetails() {
        ApiService.apiService.getProductById(productId).enqueue(new Callback<ResponseProduct>() {
            @Override
            public void onResponse(Call<ResponseProduct> call, Response<ResponseProduct> response) {
                if (response.isSuccessful() && response.body() != null) {
                    product = response.body().getProduct();
                    populateProductDetails(product);
                } else {
                    Log.e("Error", "API call failed with error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseProduct> call, Throwable t) {
                Log.e("Error", "API call failed: " + t.getMessage());
            }
        });
    }

    private void populateProductDetails(Product product) {
        binding.textProductName.setText(product.getName());
        binding.textProductPrice.setText(product.getAvrPrice() +"VNĐ");
        binding.rating.setText("("+product.getRated()+" đánh giá)");
        binding.textProductDescription.setText(product.getDescription());
        binding.ratingBar.setRating((float) product.getRating());
        binding.tvSold.setText("(Đã bán "+ product.getSold() +")");
        populateTable(product);
        productItems = product.getProductItems();
        Glide.with(this).load(product.getImageUrl()).into(binding.mainImage);
        setupStorageOptions();
        setupProductImages(imgs);
    }

    private void setupStorageOptions() {
        binding.storageOptions.removeAllViews();
        for (ProductItem item : productItems) {
            AppCompatButton button = new AppCompatButton(this);
            button.setText(item.getProductItemName());
            button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.round_back_dark_blue5_15 ));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 10, 0);
            button.setLayoutParams(layoutParams);
            button.setOnClickListener(v -> {
                if (selectedButton != null) {
                    selectedButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
                    button.setBackground(ContextCompat.getDrawable(this, R.drawable.round_back_dark_blue5_15 ));
                }
                button.setBackground(ContextCompat.getDrawable(this, R.drawable.round_back_green ));
                selectedButton = button;
                selectedPDIId = item.getId();
                selectedPDIImg = item.getImageUrl();
                selectedPDIPrice = item.getPrice();
                selectedPDIName = item.getProductItemName();
                binding.textProductPrice.setText(Utils.formatPrice(item.getPrice()));
                Glide.with(this).load(item.getImageUrl()).into(binding.mainImage);
            });
            imgs.add(item.getImageUrl());
            binding.storageOptions.addView(button);
        }
    }

    private void setupProductImages(List<String> imageUrls) {
        imgs.add(product.getImageUrl());
        Collections.swap(imgs, 0,imgs.size()-1);
        ImageAdapter adapter = new ImageAdapter(imageUrls, imageUrl -> {
            Glide.with(this).load(imageUrl).into(binding.mainImage);
        });
        binding.productImagesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.productImagesRecycler.setAdapter(adapter);
    }

    private void populateTable(Product product) {
        Map<String, String> attributes = product.getAttributes();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            TableRow row = new TableRow(this);
            TextView attributeName = new TextView(this);
            attributeName.setText(entry.getKey());
            attributeName.setPadding(8, 8, 8, 8);
            TextView attributeValue = new TextView(this);
            attributeValue.setText(entry.getValue());
            attributeValue.setPadding(8, 8, 8, 8);
            row.addView(attributeName);
            row.addView(attributeValue);
            binding.detailsTable.addView(row);
        }
    }

    private void addToCart() {
        String apiUrl = Constant.baseUrl + "customer/addToCart";

        if (selectedPDIId == null || customerId == null) {
            Toast.makeText(this, "Vui lòng chọn phận loại sản phẩm!", Toast.LENGTH_SHORT).show();
        }

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("customerId", customerId);
            requestBody.put("product_item_id", selectedPDIId);
            requestBody.put("quantity", 1);

            StringRequest request = new StringRequest(Request.Method.POST, apiUrl,
                    response -> {
                        Toast.makeText(getApplicationContext(), "Sản phẩm đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Log.e("AddToCartError", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public byte[] getBody() {
                    return requestBody.toString().getBytes();
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    if (token != null) {  // Thêm token vào headers nếu nó tồn tại
                        headers.put("Authorization", "Bearer " + token);
                    }
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(request);
        } catch (JSONException e) {
            Log.e("AddToCartError", "JSON Error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi khi tạo yêu cầu", Toast.LENGTH_SHORT).show();
        }
    }


    private void buyNow() {
        if (selectedPDIId == null || customerId == null) {
            Toast.makeText(this, "Vui lòng chọn phận loại sản phẩm!", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(DetailProductActivity.this, PayActivity.class);
            intent.putExtra("productItemId", selectedPDIId);
            intent.putExtra("detailName", product.getName());
            intent.putExtra("detailPrice", selectedPDIPrice);
            intent.putExtra("imageUrl", selectedPDIImg);
            intent.putExtra("productItemName", selectedPDIName);
            startActivity(intent);
        }

    }

}


