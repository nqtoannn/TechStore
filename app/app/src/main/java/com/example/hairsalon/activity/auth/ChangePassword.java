package com.example.hairsalon.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hairsalon.R;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.model.ResponseData;
import com.example.hairsalon.model.ResponseString;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_otp);
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        setControl();
        setEvent();
    }
    Integer userId;
    EditText edtPassword, edtConPassword;
    Button btnConfirm;
    ImageView passwordIcon,conPasswordIcon;
    private boolean passwordShow = false;
    private boolean conPasswordShow = false;


    private void setControl(){
        edtPassword =findViewById(R.id.edtPassword);
        edtConPassword =findViewById(R.id.edtConPassword);
        passwordIcon = findViewById(R.id.passwordIcon);
        conPasswordIcon = findViewById(R.id.conPasswordIcon);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void setEvent() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordShow){
                    passwordShow = false;
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.show_password);
                }
                else {
                    passwordShow = true;
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.hide_password);
                }
                edtPassword.setSelection(edtPassword.length());
            }
        });
        conPasswordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(conPasswordShow){
                    conPasswordShow = false;
                    edtConPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    conPasswordIcon.setImageResource(R.drawable.show_password);
                }
                else {
                    conPasswordShow = true;
                    edtConPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    conPasswordIcon.setImageResource(R.drawable.hide_password);
                }
                edtConPassword.setSelection(edtConPassword.length());
            }
        });

    }

    private void changePassword() {
        String pass, pass2;
        pass = edtPassword.getText().toString();
        pass2 = edtConPassword.getText().toString();
        if(!pass.equals(pass2)){
            Toast.makeText(this, "Mật khẩu chưa khớp", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 8) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu dài hơn 8 kí tự", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.equals(pass2)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", userId);
            jsonObject.addProperty("password", edtPassword.getText().toString());
            ApiService.apiService.changePassword(jsonObject).enqueue(new Callback<ResponseString>() {
                @Override
                public void onResponse(Call<ResponseString> call, Response<ResponseString> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ChangePassword.this, "Đổi mật khẩu thành công, Vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChangePassword.this, Login.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ChangePassword.this, "Đổi mật khẩu không thành công!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseString> call, Throwable t) {
                    Log.e("Error", "API call failed: " + t.getMessage());
                    Toast.makeText(ChangePassword.this, "Đổi mật khẩu không thành công!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Mật khẩu chưa khớp!", Toast.LENGTH_SHORT).show();
        }

    }

}