package com.example.hairsalon.activity.order;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.hairsalon.R;
import com.example.hairsalon.adapter.OrderHistoryAdapter;
import com.example.hairsalon.adapter.OrderItemAdapter;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.constants.Constant;
import com.example.hairsalon.databinding.ActivityOrderDetailBinding;
import com.example.hairsalon.model.CartItem;
import com.example.hairsalon.model.Order;
import com.example.hairsalon.model.OrderItem;
import com.example.hairsalon.model.ResponseData;
import com.example.hairsalon.model.ResponseString;
import com.example.hairsalon.model.User;
import com.example.hairsalon.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class OrderDetail extends AppCompatActivity {

    Integer orderId;
    ActivityOrderDetailBinding binding;
    String token;
    boolean isImageVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        orderId = intent.getIntExtra("orderId",2);
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        token = "Bearer " + sharedPreferences.getString("access_token","");
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
                                List<OrderItem> orderItems = new ArrayList<>();
                                for (Map<String, Object> itemMap : orderItemsList) {
                                    OrderItem orderItem = new OrderItem();
                                    orderItem.setOrderItemId(((Number) itemMap.get("orderItemId")).intValue());
                                    orderItem.setProductName((String) itemMap.get("productName"));
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
                                switch (order.getOrderStatus()) {
                                    case "PROCESSING":
                                        binding.tvOrderStatus.setText("Trạng thái: Đơn hàng đang chờ xác nhận");
                                        break;
                                    case "ACCEPTED":
                                        binding.tvOrderStatus.setText("Trạng thái: Người bán đang chuẩn bị hàng");
                                        break;
                                    case "CANCEL":
                                        binding.tvOrderStatus.setText("Trạng thái: Đơn hàng đã bị hủy" );
                                        break;
                                    case "DELIVERY":
                                        binding.tvOrderStatus.setText("Trạng thái: Đơn hàng đang được vận chuyển");
                                        break;
                                    case "SUCCESS":
                                    case "REVIEWED":
                                        binding.tvOrderStatus.setText("Trạng thái: Đơn hàng đã hoàn thành");
                                        break;
                                    case "PAID":
                                        binding.tvOrderStatus.setText("Trạng thái: Đơn hàng đã được thanh toán");
                                        break;
                                        case "WAITING PAYMENT":
                                        binding.tvOrderStatus.setText("Trạng thái: Đơn hàng đang chờ thanh toán");
                                        break;
                                    default:
                                        binding.tvOrderStatus.setText("Trạng thái: Đã xảy ra lỗi ở đây");
                                        break;
                                }

                                if (order.getOrderStatus().equals("PROCESSING")) {
                                    binding.line.setVisibility(View.VISIBLE);
                                    binding.buttonCancel.setVisibility(View.VISIBLE);
                                    binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showConfirmationDialog();
                                        }
                                    });
                                } else if(order.getOrderStatus().equals("WAITING PAYMENT")){
                                    binding.line.setVisibility(View.VISIBLE);
                                    binding.buttonCancel.setVisibility(View.VISIBLE);
                                    binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showConfirmationDialog();
                                        }
                                    });
                                    binding.buttonAccept.setVisibility(View.VISIBLE);
                                    binding.buttonAccept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getPaymentUrl();
                                        }
                                    });
                                } else if (order.getOrderStatus().equals("SUCCESS") ) {
                                    binding.line.setVisibility(View.VISIBLE);
                                    binding.buttonCancel.setVisibility(View.VISIBLE);
                                    binding.buttonCancel.setText("Đánh giá");
                                    binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(OrderDetail.this, OrderReview.class);
                                            intent.putExtra("orderId", orderId);
                                            startActivity(intent);
                                        }
                                    });
                                    binding.buttonAccept.setVisibility(View.VISIBLE);
                                    binding.buttonAccept.setText("Ảnh giao hàng");
                                    binding.buttonAccept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!isImageVisible) { // Nếu hình ảnh đang ẩn
                                                binding.imageView.setVisibility(View.VISIBLE); // Hiển thị hình ảnh
                                                Glide.with(OrderDetail.this)
                                                        .load(order.getNote()) // URL hình ảnh
                                                        .into(binding.imageView);
                                                binding.buttonAccept.setText("Ẩn hình ảnh"); // Đổi text thành "Ẩn hình ảnh"
                                                Toast.makeText(OrderDetail.this, "Đang tải hình ảnh...", Toast.LENGTH_SHORT).show();
                                            } else { // Nếu hình ảnh đang hiện
                                                binding.imageView.setVisibility(View.GONE); // Ẩn hình ảnh
                                                binding.buttonAccept.setText("Ảnh giao hàng"); // Đổi text thành "Ảnh giao hàng"
                                            }
                                            isImageVisible = !isImageVisible; // Đảo trạng thái
                                        }
                                    });
                                } else if (order.getOrderStatus().equals("REVIEWED")) {
                                    binding.buttonCancel.setVisibility(View.INVISIBLE);
                                    binding.buttonAccept.setVisibility(View.VISIBLE);
                                    binding.buttonAccept.setText("Ảnh giao hàng");
                                    binding.buttonAccept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!isImageVisible) { // Nếu hình ảnh đang ẩn
                                                binding.imageView.setVisibility(View.VISIBLE); // Hiển thị hình ảnh
                                                Glide.with(OrderDetail.this)
                                                        .load(order.getNote()) // URL hình ảnh
                                                        .into(binding.imageView);
                                                binding.buttonAccept.setText("Ẩn hình ảnh"); // Đổi text thành "Ẩn hình ảnh"
                                                Toast.makeText(OrderDetail.this, "Đang tải hình ảnh...", Toast.LENGTH_SHORT).show();
                                            } else { // Nếu hình ảnh đang hiện
                                                binding.imageView.setVisibility(View.GONE); // Ẩn hình ảnh
                                                binding.buttonAccept.setText("Ảnh giao hàng"); // Đổi text thành "Ảnh giao hàng"
                                            }
                                            isImageVisible = !isImageVisible; // Đảo trạng thái
                                        }
                                    });
                                } else {
                                    binding.buttonCancel.setVisibility(View.INVISIBLE);
                                }
                                if(order.getOrderStatus().equals("CANCEL")){
                                    binding.tvNote.setVisibility(View.VISIBLE);
                                    if(order.getNote().equals("null")){
                                        binding.tvNote.setText("Lý do hủy: Hủy bởi khách hàng");
                                    }else {
                                        binding.tvNote.setText("Lý do hủy: "+order.getNote());
                                    }
                                }
                                ArrayList<CartItem> cartItems = new ArrayList<>();
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
                                OrderItemAdapter orderItemAdapter = new OrderItemAdapter(OrderDetail.this, cartItems);
                                binding.recyclerViewOrder.setAdapter(orderItemAdapter);
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

    private void updateStatusOrder() {
        String apiUrl = Constant.baseUrl + "customer/updateOrderStatus";
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("id", orderId);
            requestBody.put("orderStatus", "REJECTED");

            final String requestBodyString = requestBody.toString();

            Log.i("request body", requestBodyString);

            StringRequest request = new StringRequest(Request.Method.PUT, apiUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(), "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Update status", error.getMessage());
                            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi khi hủy đơn hàng", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }) {
                @Override
                public byte[] getBody() {
                    return requestBodyString.getBytes();
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    if (token != null) {  // Thêm token vào headers nếu nó tồn tại
                        headers.put("Authorization", token);
                    }
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi khi tạo yêu cầu", Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận hủy đơn hàng!");
        builder.setMessage("Bạn có muốn hủy đơn hàng này?");
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateStatusOrder();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void getPaymentUrl(){
        ApiService.apiService.getPaymentUrlByOrderId(orderId, token).enqueue(new Callback<ResponseString>() {
            @Override
            public void onResponse(Call<ResponseString> call, retrofit2.Response<ResponseString> response) {
                ResponseString responseString = response.body();
                String url = responseString.getData();
                openUrlInDefaultBrowser(url);
                Log.e("E",url);
            }
            @Override
            public void onFailure(Call<ResponseString> call, Throwable t) {

            }
        });
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