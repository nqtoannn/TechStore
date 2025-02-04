package com.example.hairsalon.activity.auth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hairsalon.R;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.model.ResponseData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        setControl();
        setEvent();
    }

    EditText edtEmail;
    Button bthSendEmail;
    TextView txtLogin;
    ScrollView scrollView;
    private void setControl(){
        edtEmail =findViewById(R.id.edtEmail);
        bthSendEmail =findViewById(R.id.btnSendEmail);
        txtLogin =findViewById(R.id.txtLogin);
        scrollView =findViewById(R.id.scrollView);
    }

    private void setEvent() {
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPassword.this, Login.class);
                startActivity(intent);
            }
        });

        bthSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtEmail.getText().toString().isEmpty()){
                    Toast.makeText(ForgetPassword.this,"Vui lòng nhập email!",Toast.LENGTH_SHORT).show();
                }
                if(isValidEmail(edtEmail.getText().toString())){
                    ApiService.apiService.forgotPassword(edtEmail.getText().toString()).enqueue(new Callback<ResponseData>() {
                        @Override
                        public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                            Toast.makeText(ForgetPassword.this,"Vui lòng kiểm tra email của bạn!",Toast.LENGTH_LONG);
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                Intent intent = new Intent(ForgetPassword.this, Login.class);
                                startActivity(intent);
                                finish();
                            }, 5000);
                        }

                        @Override
                        public void onFailure(Call<ResponseData> call, Throwable t) {
                            Toast.makeText(ForgetPassword.this,"Vui lòng kiểm tra kết nối của bạn!",Toast.LENGTH_LONG);
                        }
                    });
                } else {
                    Toast.makeText(ForgetPassword.this,"Vui lòng nhập email hợp lệ!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            int y= edtEmail.getBottom() - 100;
                            scrollView.scrollTo(0, bthSendEmail.getTop());
                        }
                    });
                }
            }
        });
    }
    private boolean isValidEmail(CharSequence target) {
        return (target != null && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}