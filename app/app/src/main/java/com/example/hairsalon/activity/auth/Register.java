package com.example.hairsalon.activity.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hairsalon.R;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.model.ResponseAuthData;
import com.example.hairsalon.model.ResponseData;
import com.example.hairsalon.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

        EditText edtEmail, edtPassword, edtConPassword;
        AppCompatButton btnRegister;
        TextView txtLogIn;
        ImageView passwordIcon,conPasswordIcon;
        private boolean passwordShow = false;
        private boolean conPasswordShow = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setControl();
        setEvent();
    }

        private void setControl(){
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConPassword = findViewById(R.id.edtConPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogIn = findViewById(R.id.txtLogIn);
        passwordIcon = findViewById(R.id.passwordIcon);
        conPasswordIcon = findViewById(R.id.conPasswordIcon);
    }

        private  void setEvent(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        txtLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
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

        private void register() {
        String email, pass,pass2;
        email = edtEmail.getText().toString();
        pass = edtPassword.getText().toString();
        pass2 = edtConPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Vui lòng nhập email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isValidEmail(email)){
            Toast.makeText(this, "Vui lòng nhập email hợp lệ!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Vui lòng nhập mật khẩu",Toast.LENGTH_SHORT).show();
            return;
        }
        if(pass.length()<8){
            Toast.makeText(this, "Vui lòng nhập mật khẩu dài hơn 8 kí tự",Toast.LENGTH_SHORT).show();
            return;
        }
        if(pass.equals(pass2))
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", edtEmail.getText().toString());
            jsonObject.addProperty("password", edtPassword.getText().toString());
            jsonObject.addProperty("role", "CUSTOMER");
            ApiService.apiService.registerUser(jsonObject).enqueue(new Callback<ResponseAuthData>() {
                @Override
                public void onResponse(Call<ResponseAuthData> call, Response<ResponseAuthData> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(Register.this, "Đăng ký thành công! Vui lòng kiểm tra email", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Register.this,Login.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Register.this, "Cập nhật thông tin không thành công!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseAuthData> call, Throwable t) {
                    Log.e("Error", "API call failed: " + t.getMessage());
                }
            });
        }else
        {
            Toast.makeText(this, "Mật khẩu chưa khớp!",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return (target != null && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}