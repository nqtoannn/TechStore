package com.example.hairsalon.activity.manage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    private FragmentEmployeeInfoBinding binding;
    private List<Map<String, Object>> employeeList = new ArrayList<>();
    private Integer employeeId;
    String token;
    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance(Integer employeeId) {
        UserProfileFragment fragment = new UserProfileFragment();
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
        binding.lableName.setText("Thông tin tài khoản");
        binding.btnActive.setText("Sửa thông tin");
        View view = binding.getRoot();
        if (employeeId != null) {
            loadUserInfo();
        }
        binding.btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileUpdateFragment fragment =new UserProfileUpdateFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
                        binding.textUsername.setText("Mã người dùng: "+((Number) employee.get("id")).intValue());
                        binding.textEmail.setText("Email: " + employee.get("email").toString());
                        binding.textFullName.setText("Tên: " + employee.get("fullName").toString());
                        binding.textPhoneNumber.setText("Số điện thoại: " + employee.get("phoneNumber").toString());
                        binding.textAddress.setVisibility(View.GONE);
                        binding.textRole.setVisibility(View.GONE);
                        binding.textStatus.setText("Ngày tham gia: " + employee.get("createAt"));
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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}