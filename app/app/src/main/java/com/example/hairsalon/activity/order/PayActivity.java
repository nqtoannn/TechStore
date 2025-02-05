package com.example.hairsalon.activity.order;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.hairsalon.activity.cart.CartActivity;
import com.example.hairsalon.activity.home.HomeCustomer;
import com.example.hairsalon.adapter.CartItemAdapter;
import com.example.hairsalon.adapter.OrderItemAdapter;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.constants.Constant;
import com.example.hairsalon.databinding.ActivityPayBinding;
import com.example.hairsalon.model.CartItem;
import com.example.hairsalon.model.ResponseData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import com.android.volley.Response;
import com.example.hairsalon.utils.Utils;

import android.content.Intent;

public class PayActivity extends AppCompatActivity {
    ActivityPayBinding binding;
    ArrayList<Map<String, Object>> data = new ArrayList<>();
    ArrayList<Map<String, Object>> cartItemList = new ArrayList<>();
    ArrayList<CartItem> dataArrayList = new ArrayList<>();
    double price = 0.0;
    double deliveryPrice = 0;
    Integer productItemId, customerId, cartId;
    private Double totalPrice = 0.0;
    private Double finalPrice = 0.0;
    private List<Integer> paymentMethodIds = new ArrayList<>();
    private int selectedPaymentMethodId = -1;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> {
                    Intent intentNew = new Intent(PayActivity.this, HomeCustomer.class);
                    startActivity(intentNew);
                }
        );
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        customerId = sharedPreferences.getInt("userId", 2);
        token = "Bearer " + sharedPreferences.getString("access_token","");
        Intent intent = getIntent();
        if (intent.getStringExtra("detailName") != null) {
            binding.linearLayout.setVisibility(View.VISIBLE);
            binding.listViewProducts.setVisibility(View.GONE);
            String name = intent.getStringExtra("detailName");
            price = intent.getDoubleExtra("detailPrice", 0);
            String imageUrl = intent.getStringExtra("imageUrl");
            String productItemName = intent.getStringExtra("productItemName");
            productItemId = intent.getIntExtra("productItemId", 0);
            binding.productName.setText(name);
            binding.productPrice.setText("Giá: "+Utils.formatPrice(price));
            binding.productQuantity.setText("Số lượng: 1");
            binding.productItemName.setText("Phân loại "+ productItemName);
            binding.productQuantity.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUrl).into(binding.productImage);
        } else {
            binding.linearLayout.setVisibility(View.GONE);
            binding.listViewProducts.setVisibility(View.VISIBLE);
            List<CartItem> selectedItems = (List<CartItem>) intent.getSerializableExtra("selectedItems");
            for(CartItem cartItem: selectedItems){
                totalPrice += cartItem.getPrice() * cartItem.getQuantity();
                Log.e("TAG", "onCreate: "+totalPrice );
            }
            Log.e("total",totalPrice.toString());
            Collections.reverse(selectedItems);
            ArrayList<CartItem> dataArrayList = new ArrayList<>(selectedItems);
            OrderItemAdapter orderItemAdapter = new OrderItemAdapter(PayActivity.this, dataArrayList);
            binding.listViewProducts.setAdapter(orderItemAdapter);
        }
        ApiService.apiService.getCustomerById(customerId, token).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, retrofit2.Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    ResponseData responseData = response.body();
                    if (responseData != null && responseData.getStatus().equals("OK")) {
                        data = (ArrayList<Map<String, Object>>) responseData.getData();
                        Map<String, Object> dataItem = data.get(0);
                        binding.textCustomerName.setText(dataItem.get("fullName").toString());
                        binding.textPhoneNumber.setText(dataItem.get("phoneNumber").toString());
                    } else {
                        Log.e("Error", "No cart item data found in response");
                    }
                } else {
                    Log.e("Error", "API call failed with error code: Address" + response.code());
                }
            }
            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.e("Error", "API call failed: " + t.getMessage());
            }
        });
        setupSpinners();
        binding.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
        binding.spinnerDeliveryMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String deliveryMethod = parent.getItemAtPosition(position).toString();
                if (deliveryMethod.equals("Giao hàng nhanh")) {
                    deliveryPrice = 20000;
                } else if (deliveryMethod.equals("Giao hàng tiết kiệm")) {
                    deliveryPrice = 0;
                } else if (deliveryMethod.equals("Giao hàng hỏa tốc")) {
                    deliveryPrice = 50000;
                }
                if (intent.getStringExtra("detailName") != null) {
                    finalPrice = price + deliveryPrice;
                    binding.totalPrice.setText(Utils.formatPrice(finalPrice));
                } else {
                    finalPrice = totalPrice + deliveryPrice;
                    binding.totalPrice.setText(Utils.formatPrice(finalPrice));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(PayActivity.this, "Vui lòng chọn phương thức vận chuyển", Toast.LENGTH_SHORT).show();
            }
        });
        binding.spinnerPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPaymentMethodId = paymentMethodIds.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPaymentMethodId = -1;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupSpinners() {
        List<String> paymentMethods = new ArrayList<>();
        String[] deliveryMethods = {"Giao hàng nhanh", "Giao hàng tiết kiệm", "Giao hàng hỏa tốc"};
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        ArrayAdapter<String> deliveryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, deliveryMethods);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deliveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ApiService.apiService.getAllPayment().enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, retrofit2.Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    ResponseData responseData = response.body();
                    if (responseData != null && "OK".equals(responseData.getStatus())) {
                        List<Map<String, Object>> paymentData = responseData.getData();
                        paymentMethods.clear();
                        for (Map<String, Object> payment : paymentData) {
                            String paymentName = (String) payment.get("name");
                            Integer id = ((Number) payment.get("id")).intValue();
                            paymentMethods.add(paymentName);
                            paymentMethodIds.add(id);
                        }
                        paymentAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Error", "Không tìm thấy dữ liệu phương thức thanh toán trong phản hồi");
                    }
                } else {
                    Log.e("Error", "API thất bại với mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.e("Error", "API thất bại: " + t.getMessage());
            }
        });
        binding.spinnerPaymentMethod.setAdapter(paymentAdapter);
        binding.spinnerDeliveryMethod.setAdapter(deliveryAdapter);

    }


    private void createOrder() {
        String apiUrl = Constant.baseUrl + "customer/order";
        try {
            String address = String.valueOf(binding.textPhoneNumber.getText());
            JSONObject requestBody = new JSONObject();
            requestBody.put("customerId", customerId);
            requestBody.put("payId", selectedPaymentMethodId);
            requestBody.put("address", address);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new Date());
            requestBody.put("orderDate", currentDate);
            JSONArray orderItemListJson = new JSONArray();
            if (price != 0.0) {
                requestBody.put("totalPrice", finalPrice);
                JSONObject orderItemJson = new JSONObject();
                orderItemJson.put("productItemId", productItemId);
                orderItemJson.put("price", price);
                orderItemJson.put("quantity", 1);
                orderItemListJson.put(orderItemJson);
                requestBody.put("orderItemList", orderItemListJson);
            }
            else {
                for (Map<String, Object> cartItem : cartItemList) {
                    JSONObject orderItemJson = new JSONObject();
                    orderItemJson.put("productItemId", cartItem.get("productItemId"));
                    orderItemJson.put("price", cartItem.get("price"));
                    orderItemJson.put("quantity", cartItem.get("quantity"));
                    orderItemListJson.put(orderItemJson);
                }
                requestBody.put("totalPrice", totalPrice);
                requestBody.put("orderItemList", orderItemListJson);
            }

            StringRequest request = new StringRequest(Request.Method.POST, apiUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                // Phân tích JSON từ response
                                JSONObject jsonResponse = new JSONObject(response);

                                // Lấy giá trị từ trường "data"
                                String paymentUrl = jsonResponse.optString("data", "");
                                Log.e("E",paymentUrl);
                                Intent intent = new Intent(PayActivity.this, SuccessPay.class);
                                if (!paymentUrl.isEmpty()) {
                                    intent.putExtra("payment_Url", paymentUrl); // Truyền URL vào Intent
                                }

                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Lỗi khi xử lý phản hồi từ server", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error response", String.valueOf(error));
                            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi khi thanh toán đơn hàng", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                public byte[] getBody() {
                    return requestBody.toString().getBytes();
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", token);
                    return headers;
                }
            };
            Volley.newRequestQueue(this).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận đặt hàng");
        builder.setMessage("Xác nhận đặt đơn hàng này?");
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createOrder();
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

}