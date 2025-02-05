package com.example.hairsalon.activity.manage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hairsalon.R;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.databinding.FragmentEmployeeInfoBinding;
import com.example.hairsalon.model.ResponseData;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EmployeeInfoFragment extends Fragment {

    private FragmentEmployeeInfoBinding binding;
    private List<Map<String, Object>> employeeList = new ArrayList<>();
    private Integer employeeId;
    String token;
    public EmployeeInfoFragment() {
        // Required empty public constructor
    }

    public static EmployeeInfoFragment newInstance(Integer employeeId) {
        EmployeeInfoFragment fragment = new EmployeeInfoFragment();
        Bundle args = new Bundle();
        args.putInt("employeeId",employeeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            employeeId = getArguments().getInt("employeeId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmployeeInfoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (employeeId != null) {
            loadUserInfo();
        }
        binding.btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", employeeId);
                Map<String, Object> employee = employeeList.get(0);
                if(employee.get("status").equals("OK")){
                    jsonObject.addProperty("status", "NOT_OK");
                }
                else {
                    jsonObject.addProperty("status", "OK");
                }
                ApiService.apiService.updateUserStatus(jsonObject).enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(),"Thay đổi trạng thái tài khoản thành công!",Toast.LENGTH_SHORT);
                            loadUserInfo();
                        } else {
                            Toast.makeText(requireContext(),"Thay đổi trạng thái tài khoản không thành công!",Toast.LENGTH_SHORT);
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        Log.e("Error", "API call failed: " + t.getMessage());
                    }
                });
            }
        });

        return view;
    }

    private void loadUserInfo() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE);

        token = "Bearer " + sharedPreferences.getString("access_token","");
        ApiService.apiService.getEmployeeById(employeeId, token).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    ResponseData responseData = response.body();
                    if (responseData != null && responseData.getStatus().equals("OK")) {
                        employeeList = responseData.getData();
                        Map<String, Object> employee = employeeList.get(0);
                        binding.textUsername.setText("Tên người dùng: "+employee.get("userName").toString());
                        binding.textEmail.setText("Email: " + employee.get("email").toString());
                        binding.textAddress.setText("Địa chỉ: " + employee.get("address").toString());
                        binding.textFullName.setText("Tên: " + employee.get("fullName").toString());
                        binding.textPhoneNumber.setText("Số điện thoại: " + employee.get("phoneNumber").toString());
                        binding.textRole.setText("Chức vụ: " + employee.get("role").toString());
                        if(employee.get("status").toString().equals("OK")){
                            binding.textStatus.setText("Trạng thái: Hoạt động");
                            binding.btnActive.setText("Khóa tài khoản");
                        }else {
                            binding.textStatus.setText("Trạng thái: Không hoạt động");
                            binding.btnActive.setText("Kích hoạt");
                        }
                    } else {
                        Log.e("Error", "No product data found in response");
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