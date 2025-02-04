package com.example.hairsalon.activity.employee;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hairsalon.R;
import com.example.hairsalon.adapter.OrderItemAdapter;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.constants.Constant;
import com.example.hairsalon.databinding.ActivityOrderDetailEmployeeBinding;
import com.example.hairsalon.model.CartItem;
import com.example.hairsalon.model.Order;
import com.example.hairsalon.model.OrderItem;
import com.example.hairsalon.model.ResponseData;
import com.example.hairsalon.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class OrderDetailEmployee extends AppCompatActivity {

    Integer orderId;
    ActivityOrderDetailEmployeeBinding binding;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailEmployeeBinding.inflate(getLayoutInflater());
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
                                        binding.tvOrderStatus.setText("Trạng thái: Đơn hàng đã bị hủy");
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
                                    binding.buttonCancel.setVisibility(View.VISIBLE);
                                    binding.buttonAccept.setVisibility(View.VISIBLE);
                                    binding.buttonAccept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showConfirmationDialog();
                                            }
                                    });
                                    binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showCancelDialog();
                                        }
                                    });
                                } else if(order.getOrderStatus().equals("DELIVERY")) {
                                    binding.buttonCancel.setVisibility(View.VISIBLE);
                                    binding.buttonAccept.setVisibility(View.VISIBLE);
                                    binding.buttonAccept.setText("Đã giao hàng");
                                    binding.buttonAccept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            startActivityForResult(camera_intent, 123);
                                        }
                                    });
                                    binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showCancelDialog();
                                        }
                                    });
                                } else {
                                    binding.buttonCancel.setVisibility(View.INVISIBLE);
                                    binding.buttonAccept.setVisibility(View.INVISIBLE);
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
                                OrderItemAdapter orderItemAdapter = new OrderItemAdapter(OrderDetailEmployee.this, cartItems);
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
        String apiUrl = Constant.baseUrl + "employee/order/updateStatusOrder";
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("orderId", orderId);
            requestBody.put("statusCode", 2);
            final String requestBodyString = requestBody.toString();
            Log.i("request body", requestBodyString);
            StringRequest request = new StringRequest(Request.Method.PUT, apiUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(), "Xác nhận đơn hàng thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("update status", error.getMessage());
                            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi khi xác nhận đơn hàng", Toast.LENGTH_SHORT).show();
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
        builder.setTitle("Xác nhận đơn hàng!");
        builder.setMessage("Bạn có muốn xác nhận đơn hàng này?");
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

    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận đơn hàng!");
        builder.setMessage("Bạn có muốn hủy đơn hàng này?");
        final EditText inputReason = new EditText(this);
        inputReason.setHint("Nhập lý do hủy");
        inputReason.setPadding(16, 16, 16, 16);
        inputReason.setBackground(ContextCompat.getDrawable(this, R.drawable.round_back_dark_blue5_15 ));
        builder.setView(inputReason);
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cancelReason = inputReason.getText().toString();
                if (!cancelReason.isEmpty()) {
                    updateCancelOrder(cancelReason);
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập lý do hủy!", Toast.LENGTH_SHORT).show();
                }
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

    private void updateCancelOrder(String note) {
        String apiUrl = Constant.baseUrl + "employee/order/updateStatusOrder";
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("orderId", orderId);
            requestBody.put("statusCode", 8);
            requestBody.put("note", note);
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
                        headers.put("Authorization",token);
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            File photoFile = saveBitmapToFile(photo);
            if (photoFile != null) {
                MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                        "file", // Tên tham số trùng với định nghĩa API
                        photoFile.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), photoFile)
                );

                String namePath = "confirmOrder"; // Tên thư mục hoặc đường dẫn bạn muốn lưu
                Call<Void> call = ApiService.apiService.uploadOrderFile(filePart, namePath, orderId, token);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                            Toast.makeText(OrderDetailEmployee.this, "Thành công", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(OrderDetailEmployee.this, "Thất bại", Toast.LENGTH_SHORT).show();
                            Log.e("Failure", t.toString());
                        }
                    });
                        } else {
                            Toast.makeText(this, "Không thể lưu ảnh!", Toast.LENGTH_SHORT).show();
                        }
                    }
    }
    private File saveBitmapToFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}