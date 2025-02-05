package com.example.hairsalon.activity.employee;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.hairsalon.adapter.OrderHistoryAdapter;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.databinding.FragmentBookingListBinding;
import com.example.hairsalon.model.Order;
import com.example.hairsalon.model.OrderItem;
import com.example.hairsalon.model.ResponseData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderListEmployeeFragment extends Fragment {

    private FragmentBookingListBinding binding;

    OrderHistoryAdapter orderHistoryAdapter;
    ArrayList<Order> dataArrayList = new ArrayList<>();
    public OrderListEmployeeFragment() {

    }

    Integer employeeId;
    Integer orderStatusId = 0;
    String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookingListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        employeeId = sharedPreferences.getInt("userId", 1);
        token = "Bearer " + sharedPreferences.getString("access_token","");
        List<String> filterOptions = Arrays.asList("Đã gói hàng", "Đang vận chuyển");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, filterOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFilter.setAdapter(adapter);
        binding.spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = filterOptions.get(position);
                orderStatusId = 2;
                switch (selectedFilter) {
                    case "Đã gói hàng":
                        orderStatusId = 2;
                        break;
                    case "Đang vận chuyển":
                        orderStatusId = 3;
                        break;
                }
                getOrderWithStatus(orderStatusId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void getOrderWithStatus(Integer statusId){
        ApiService.apiService.getOrderWithStatus(statusId, token).enqueue(new Callback<ResponseData>() {
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
                        if(dataArrayList.isEmpty()){
                            binding.listViewOrderHistory.setVisibility(View.GONE);
                        }else {
                            binding.listViewOrderHistory.setVisibility(View.VISIBLE);
                        }
                        Collections.reverse(dataArrayList);
                        orderHistoryAdapter = new OrderHistoryAdapter(requireContext() ,dataArrayList);
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
    public void onResume() {
        super.onResume();
        getOrderWithStatus(orderStatusId);
    }
}