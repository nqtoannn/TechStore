package com.example.hairsalon.activity.order;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hairsalon.adapter.AddReviewAdapter;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.constants.Constant;
import com.example.hairsalon.databinding.ActivityOrderDetailBinding;
import com.example.hairsalon.model.CartItem;
import com.example.hairsalon.model.Order;
import com.example.hairsalon.model.OrderItem;
import com.example.hairsalon.model.ResponseData;
import com.example.hairsalon.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderReview extends AppCompatActivity implements AddReviewAdapter.ReviewActionListener {
    private Map<Integer, ReviewData> reviewDataMap = new HashMap<>();
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri selectedImageUri;
    Integer orderId;
    ActivityOrderDetailBinding binding;
    ArrayList<CartItem> cartItems = new ArrayList<>();
    List<OrderItem> orderItems = new ArrayList<>();
    String token;
    private static class ReviewData {
        Integer productItemId;
        float rating;
        String comment;
        Uri imageUri;

        ReviewData( Integer productItemId, float rating, String comment, Uri imageUri) {
            this.productItemId = productItemId;
            this.rating = rating;
            this.comment = comment;
            this.imageUri = imageUri;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        orderId = intent.getIntExtra("orderId", 2);
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        token = "Bearer " + sharedPreferences.getString("access_token","");
        // Register gallery launcher
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                selectedImageUri = result.getData().getData();
                int position = (int) binding.recyclerViewOrder.getTag(); // Lấy vị trí hiện tại
                ReviewData data = reviewDataMap.get(position);
                if (data != null) {
                    data.imageUri = selectedImageUri;
                } else {
                    reviewDataMap.put(position, new ReviewData(0,0, "", selectedImageUri));
                }
            }
        });

        if (intent != null) {
            ApiService.apiService.getOrderById(orderId, token).enqueue(new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, retrofit2.Response<ResponseData> response) {
                    if (response.isSuccessful()) {
                        ResponseData responseData = response.body();
                        if (responseData != null && responseData.getStatus().equals("OK")) {
                            List<Map<String, Object>> orderList = responseData.getData();
                            if (orderList != null && !orderList.isEmpty()) {
                                Map<String, Object> orderMap = orderList.get(0);
                                Order order = new Order();
                                order.setId(((Number) orderMap.get("id")).intValue());
                                order.setOrderDate((String) orderMap.get("orderDate"));
                                order.setPaymentMethod((String) orderMap.get("paymentMethod"));
                                order.setTotalPrice((Double) orderMap.get("totalPrice"));
                                order.setOrderStatus((String) orderMap.get("orderStatus"));
                                order.setAddress((String) orderMap.get("address"));
                                order.setNote((String) orderMap.get("note"));
                                List<Map<String, Object>> orderItemsList = (List<Map<String, Object>>) orderMap.get("orderItems");

                                for (Map<String, Object> itemMap : orderItemsList) {
                                    OrderItem orderItem = new OrderItem();
                                    orderItem.setOrderItemId(((Number) itemMap.get("orderItemId")).intValue());
                                    orderItem.setProductName((String) itemMap.get("productName"));
                                    orderItem.setProductItemId(((Number) itemMap.get("productItemId")).intValue());
                                    orderItem.setProductItemName((String) itemMap.get("productItemName"));
                                    orderItem.setQuantity(((Number) itemMap.get("quantity")).intValue());
                                    orderItem.setProductItemUrl((String) itemMap.get("productItemUrl"));
                                    orderItem.setPrice((Double) itemMap.get("price"));
                                    orderItems.add(orderItem);
                                }
                                order.setOrderItems(orderItems);
                                binding.tvOrderId.setText("Mã đơn hàng: "+ order.getId());
                                binding.tvOrderDate.setText("Ngày đặt hàng: " + order.getOrderDate());
                                binding.paymentMethod.setText("Phương thức thanh toán: " + order.getPaymentMethod());
                                binding.totalPrice.setText("Tổng cộng: " + Utils.formatPrice(order.getTotalPrice()));
                                binding.address.setText("Địa chỉ: " + order.getAddress());
                                binding.tvOrderStatus.setText("Trạng thái: Đơn hàng đã hoàn thành");
                                binding.buttonCancel.setVisibility(View.VISIBLE);
                                binding.buttonCancel.setText("Đánh giá");
                                binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addReview();
                                    }
                                });
                                for (OrderItem orderItem : orderItems) {
                                    CartItem cartItem = new CartItem();
                                    cartItem.setId(orderItem.getOrderItemId());
                                    cartItem.setProductName(orderItem.getProductName());
                                    cartItem.setProductItemName(orderItem.getProductItemName());
                                    cartItem.setQuantity(orderItem.getQuantity());
                                    cartItem.setImageUrl(orderItem.getProductItemUrl());
                                    cartItem.setPrice(orderItem.getPrice());
                                    cartItems.add(cartItem);
                                }
                                AddReviewAdapter addReviewAdapter = new AddReviewAdapter(OrderReview.this, cartItems, OrderReview.this);
                                binding.recyclerViewOrder.setAdapter(addReviewAdapter);
                            } else {
                                Log.e("Error", "No order data found in response");
                            }
                        } else {
                            Log.e("Error", "Invalid response status");
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

    @Override
    public void onRatingChanged(int position, float rating) {
        ReviewData data = reviewDataMap.get(position);
        if (data != null) {
            data.rating = rating;
        } else {
            reviewDataMap.put(position, new ReviewData(0, rating, "", null));
        }
    }

    @Override
    public void onCommentChanged(int position, String comment) {
        ReviewData data = reviewDataMap.get(position);
        if (data != null) {
            data.comment = comment;
        } else {
            reviewDataMap.put(position, new ReviewData(0,0, comment, null));
        }
    }

    @Override
    public void onPickImage(int position) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        binding.recyclerViewOrder.setTag(position); // Lưu vị trí hiện tại
        galleryLauncher.launch(intent);
    }

    private void addReview() {
        String apiUrl = Constant.baseUrl + "customer/addReview";
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("orderId", orderId);
            JSONArray ratingValueArray = new JSONArray();
            for (Map.Entry<Integer, ReviewData> entry : reviewDataMap.entrySet()) {
                ReviewData data = entry.getValue();
                JSONObject reviewObject = new JSONObject();
                reviewObject.put("productItemId", orderItems.get(entry.getKey()).getProductItemId());
                reviewObject.put("ratingValue", data.rating);
                reviewObject.put("comment", data.comment);
//                reviewObject.put("imageUrl", data.imageUri != null ? data.imageUri.toString() : "null");
                reviewObject.put("imageUrl", "null");
                ratingValueArray.put(reviewObject);
            }
            requestBody.put("reviews", ratingValueArray);
            Log.e("body", requestBody.toString());
            StringRequest request = new StringRequest(Request.Method.POST, apiUrl,
                    response -> {
                        try {
                            // Parse the response to get the list of review IDs
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray reviewIds = jsonResponse.getJSONArray("data"); // Assuming response format {"status": "OK", "data": [1, 2, 3]}
                            uploadReviewImages(reviewIds);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Lỗi khi gửi đánh giá", Toast.LENGTH_SHORT).show()) {
                @Override
                public byte[] getBody() {
                    return requestBody.toString().getBytes();
                }
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    if (token != null) {
                        headers.put("Authorization", token);
                    }
                    Log.e("Headers", headers.toString());
                    return headers;
                }

            };
            Volley.newRequestQueue(this).add(request);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tạo request", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadReviewImages(JSONArray reviewIds) {
        for (int i = 0; i < reviewIds.length(); i++) {
            try {
                int reviewId = reviewIds.getInt(i);
                ReviewData reviewData = reviewDataMap.get(i); // Assuming index corresponds to position
                if (reviewData != null && reviewData.imageUri != null) {
                    uploadImage(reviewData.imageUri, reviewId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Uri imageUri, int reviewId) {
        File file = new File(imageUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call<ResponseData> call = ApiService.apiService.uploadImgReview(body, "review-images", reviewId, token);
        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplicationContext(), "Tải ảnh lên thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi kết nối khi tải ảnh", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
